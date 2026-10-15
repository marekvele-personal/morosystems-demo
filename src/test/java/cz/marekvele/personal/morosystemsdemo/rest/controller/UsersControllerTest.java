package cz.marekvele.personal.morosystemsdemo.rest.controller;

import cz.marekvele.personal.morosystemsdemo.model.User;
import cz.marekvele.personal.morosystemsdemo.rest.errorhandling.ApiExceptionHandler;
import cz.marekvele.personal.morosystemsdemo.rest.exception.UserNotFoundException;
import cz.marekvele.personal.morosystemsdemo.service.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ApiExceptionHandler.class)
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsersService usersService;

    @Test
    void getUserReturnsUserWhenUserExists() throws Exception {
        given(usersService.getUser(7L))
                .willReturn(Optional.of(new User(7L, "Jiří Svoboda")));

        mockMvc.perform(get("/rest/user/{id}", 7))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Jiří Svoboda"));
    }

    @Test
    void getUserReturnsNotFoundWhenUserDoesNotExist() throws Exception {
        given(usersService.getUser(42L))
                .willThrow(new UserNotFoundException(42L));

        mockMvc.perform(get("/rest/user/{id}", 42))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.messages[0]")
                        .value("User with id '42' was not found"))
                .andExpect(jsonPath("$.path").value("/rest/user/42"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void getUserReturnsBadRequestWhenIdIsNotNumeric() throws Exception {
        mockMvc.perform(get("/rest/user/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.messages[0]")
                        .value("Path parameter 'id' must be a valid number"))
                .andExpect(jsonPath("$.path").value("/rest/user/abc"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void getUserReturnsBadRequestWhenIdIsNegative() throws Exception {
        mockMvc.perform(get("/rest/user/{id}", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.messages[0]")
                        .value("Parameter 'id' must be greater than 0"))
                .andExpect(jsonPath("$.path").value("/rest/user/-1"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
