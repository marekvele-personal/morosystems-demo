package cz.marekvele.personal.morosystemsdemo.rest.controller;

import cz.marekvele.personal.morosystemsdemo.model.CreateOrUpdateUser;
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
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    void getUsersReturnsUsers() throws Exception {
        given(usersService.getUsers()).willReturn(java.util.List.of(
                new User(1L, "První uživatel"),
                new User(2L, "Druhý uživatel")
        ));

        mockMvc.perform(get("/rest/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("První uživatel"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Druhý uživatel"));

        then(usersService).should().getUsers();
    }

    @Test
    void createUserReturnsCreatedUser() throws Exception {
        given(usersService.createUser(new CreateOrUpdateUser("Nový uživatel")))
                .willReturn(new User(3L, "Nový uživatel"));

        mockMvc.perform(post("/rest/user")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Nový uživatel"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Nový uživatel"));

        then(usersService).should().createUser(new CreateOrUpdateUser("Nový uživatel"));
    }

    @Test
    void updateUserReturnsUpdatedUser() throws Exception {
        given(usersService.getUser(7L))
                .willReturn(Optional.of(new User(7L, "Původní jméno")));
        given(usersService.updateUser(new User(7L, "Nové jméno")))
                .willReturn(new User(7L, "Nové jméno"));

        mockMvc.perform(put("/rest/user/{id}", 7)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Nové jméno"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Nové jméno"));

        then(usersService).should().getUser(7L);
        then(usersService).should().updateUser(new User(7L, "Nové jméno"));
    }

    @Test
    void updateUserReturnsNotFoundWhenUserDoesNotExist() throws Exception {
        given(usersService.getUser(7L))
                .willThrow(new UserNotFoundException(7));

        mockMvc.perform(put("/rest/user/{id}", 7).contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("""
                        {"name":"Nové jméno"}
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.messages[0]")
                        .value("User with id '7' was not found"))
                .andExpect(jsonPath("$.path").value("/rest/user/7"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void deleteUserReturnsNoContent() throws Exception {
        given(usersService.getUser(7L))
                .willReturn(Optional.of(new User(7L, "Uživatel")));

        mockMvc.perform(delete("/rest/user/{id}", 7))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        then(usersService).should().getUser(7L);
        then(usersService).should().deleteUser(7L);
    }

    @Test
    void deleteUserReturnsNotFoundWhenUserDoesNotExist() throws Exception {
        given(usersService.getUser(7L))
                .willThrow(new UserNotFoundException(7L));

        mockMvc.perform(delete("/rest/user/{id}", 7))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.messages[0]")
                        .value("User with id '7' was not found"))
                .andExpect(jsonPath("$.path").value("/rest/user/7"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
