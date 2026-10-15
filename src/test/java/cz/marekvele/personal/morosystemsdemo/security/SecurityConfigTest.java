package cz.marekvele.personal.morosystemsdemo.security;

import cz.marekvele.personal.morosystemsdemo.constants.Profiles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(Profiles.TEST)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRestEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/rest/user"))
                .andExpect(status().isOk());
    }

    @Test
    void postRestEndpointIsPublic() throws Exception {
        mockMvc.perform(post("/rest/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Test","username":"testuser"}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteRestEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(delete("/rest/user/1"))
                .andExpect(status().isUnauthorized());
    }
}
