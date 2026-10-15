package cz.marekvele.personal.morosystemsdemo;

import cz.marekvele.personal.morosystemsdemo.constants.Profiles;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(Profiles.TEST)
class ApplicationTest {

	@Test
	void contextLoads() {
	}

}
