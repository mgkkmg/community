package com.gym.modulecore;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Core 모듈에서는 SpringBootApplication 어노테이션이 존재하지 않기 떄문에 Spring Context를 로드 할수가 없다.
 * 해당 문제를 해결하기 위해 @SpringBootApplication 어노테이션을 가지고 있는 클래스를 하나 생성하여
 * 도메인 모듈 통합 테스트를 실행할 때 Spring Context와 모든 Spring Bean을 로드하는 테스트 환경을 만들어 준다.
 */
@SpringBootApplication
public class TestConfiguration {
    public void contextLoads() {
    }
}
