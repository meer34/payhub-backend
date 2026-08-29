package com.mir.payhub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "application.security.jwt.secret-key=cGF5aHViLXRlc3Qtc2lnbmluZy1rZXktZm9yLXRlc3RzLWFuZC1ub3QtcHJvZHVjdGlvbg==")
@ActiveProfiles("test")
class PayhubBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
