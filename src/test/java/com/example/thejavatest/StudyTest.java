package com.example.thejavatest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StudyTest {

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

    @Test
    @DisplayName("assertion exception test")
    void AssertionTest_Exception() {
        // 예외 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        // 예외 메시지 기대값 확인
        assertEquals("limit은 0보다 커야 한다.", exception.getMessage());
    }

    @Test
    @DisplayName("assertion timeout test")
    void AssertionTimeOut() {
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
    void assumption_env() {
        String test_env = System.getenv("TEST_ENV");
        System.out.println(test_env);
        assertTrue("LOCAL".equalsIgnoreCase(test_env));
    }

    @Test
    @DisplayName("조건에 따라 테스트 실행하기 - OS")
    @EnabledOnOs({OS.MAC, OS.LINUX}) // @DisabledOnOs로 비활성화
    @Disabled
    void assumption_OS() {
        String test_env = System.getenv("TEST_ENV");
        System.out.println(test_env);
        assertTrue("LOCAL".equalsIgnoreCase(test_env));
    }

    @Test
    @DisplayName("조건에 따라 테스트 실행하기 - java 버젼")
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_11, JRE.JAVA_13}) // @DisabledOnJre로 비활성화
    @Disabled
    void assumption_java_version() {
        String test_env = System.getenv("TEST_ENV");
        System.out.println(test_env);
        assertTrue("LOCAL".equalsIgnoreCase(test_env));
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("before all");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("after all");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("Before each");
    }

    @AfterEach
    void afterEach() {
        System.out.println("After each");
    }

}