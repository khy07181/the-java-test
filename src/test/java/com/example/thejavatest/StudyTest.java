package com.example.thejavatest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StudyTest {

    @Order(1)
    @Test
    @DisplayName("assertion test")
    void AssertionTest() {
        Study study = new Study(10);
        // assertAll로 묶으면 assertion 중 실패 케이스 확인 가능, 묶지않으면 처음 실패한곳에서 실패
        assertAll(
                () -> assertNotNull(study),
                () -> assertEquals(StudyStatus.DRAFT, study.getStatus(), "스터디를 처음 만들면 상태값이 " + StudyStatus.DRAFT + " 여야 한다."),
                () -> assertTrue(study.getLimit() > 0, "스터디 최대 참석 가능 인원은 0보다 커야 한다.")
        );
    }

    @Order(2)
    @Test
    @DisplayName("assertion exception test")
    void AssertionExceptionTest() {
        // 예외 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        // 예외 메시지 기대값 확인
        assertEquals("limit은 0보다 커야 한다.", exception.getMessage());
    }

    @Order(3)
    @Test
    @DisplayName("assertion timeout test")
    void AssertionTimeOutTest() {
        assertTimeout(Duration.ofMillis(100), () -> {
            new Study(10);
            // Thread.sleep(300); - 300ms 경과 후 실패
            Thread.sleep(10);
        });

        // assertTimeout은 실제 걸린 시간을 기다린다.
        // assertTimeoutPreemptively을 사용하면 기준 시간을 초과할 경우 실패
        // 별도의 thread에서 실행하기 때문에 ThreadLocal을 사용하는 코드의 경우 예상치 못한 결과가 발생할 수 있다.
        // spring transaction의 경우 기본적으로 TreadLocal을 사용한다. -> 별개의 transaction에서 테스트가 돌아서 rollback이 안될 수 있다.
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            new Study(10);
            // Thread.sleep(300); - 100ms 초과할 경우 바로 실패
            Thread.sleep(10);
        });
    }

    @Test
    @DisplayName("조건에 따라 테스트 실행하기 - 환경변수")
    @EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "TEST")
    @Disabled
    void assumptionEnv() {
        String test_env = System.getenv("TEST_ENV");
        System.out.println(test_env);
        assertTrue("LOCAL".equalsIgnoreCase(test_env));
    }

    @Test
    @DisplayName("조건에 따라 테스트 실행하기 - OS")
    @EnabledOnOs({OS.MAC, OS.LINUX}) // @DisabledOnOs로 비활성화
    @Disabled
    void assumptionOS() {
        String test_env = System.getenv("TEST_ENV");
        System.out.println(test_env);
        assertTrue("LOCAL".equalsIgnoreCase(test_env));
    }

    @Test
    @DisplayName("조건에 따라 테스트 실행하기 - java 버젼")
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_11, JRE.JAVA_13}) // @DisabledOnJre로 비활성화
    @Disabled
    void assumptionJavaVersion() {
        String test_env = System.getenv("TEST_ENV");
        System.out.println(test_env);
        assertTrue("LOCAL".equalsIgnoreCase(test_env));
    }

    @Test
    @DisplayName("tagging test - fast")
    @Tag("fast") // fast가 tagging되어 있는 테스트만 실행 : edit configuration - tags
    void taggingTest() {
        Study study = new Study(10);
        assertNotNull(study);
    }

    @Test
    @DisplayName("fast custom tag test")
    @FastTest // JUnit이 제공하는 annotation은 meta annotation으로 사용할 수 있다.
    void fastCustomTagTest() {
        Study study = new Study(10);
        assertNotNull(study);
    }

    @Test
    @DisplayName("slow custom tag test")
    @SlowTest
    void slowCustomTagTest() {
        Study study = new Study(10);
        assertNotNull(study);
    }

    @DisplayName("Repetition test")
    @RepeatedTest(value = 10, name = "{displayName}, {currentRepetition}/{totalRepetitions}")
    void RepetitionTest(RepetitionInfo repetitionInfo) {
        System.out.println("test " + repetitionInfo.getCurrentRepetition() + "/" +
                repetitionInfo.getTotalRepetitions());
    }

    @DisplayName("parameterized test")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @ValueSource(strings = {"날씨가", "많이", "추워지고", "있네요."})
    @NullAndEmptySource // @EmptySource(parameter에 빈 문자열 추가), @NullSource(parameter에 Null 추가)
    void parameterizedTest(String message) {
        System.out.println(message);
    }

    @DisplayName("명시적인 타입 변환 test")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @ValueSource(ints = {10, 20, 40})
    void convertWithTest(@ConvertWith(StudyConverter.class) Study study) {
        System.out.println(study.getLimit());
    }

    static class StudyConverter extends SimpleArgumentConverter { // 하나의 argument
        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType,  "Can only convert to Study");
            return new Study(Integer.parseInt(source.toString()));
        }
    }

    @DisplayName("ArgumentsAccessor test")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"10, java study", "20, 'spring'"})
    void argumentsAccessorTest(ArgumentsAccessor accessor) { // ArgumentAccessor 인자값을 조합해서 하나로 받아주는 역할
        Study study = new Study(accessor.getInteger(0), accessor.getString(1));
        System.out.println(study);

    }

    @DisplayName("ArgumentsAggregator test")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"10, java study", "20, 'spring'"})
    void argumentsAggregatorTest(@AggregateWith(StudyAggregator.class) Study study) {
        System.out.println(study);
    }

    // ArgumentAccessor를 사용했을 때 인스턴스를 만드는 작업을 안해도 된다.
    // 반드시 static inner class 또는 public이어야 한다.
    static class StudyAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
            return new Study(accessor.getInteger(0), accessor.getString(1));
        }
    }

}