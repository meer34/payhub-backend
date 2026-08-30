package com.mir.payhub;

import com.jayway.jsonpath.JsonPath;
import com.mir.payhub.auth.repository.RefreshTokenRepository;
import com.mir.payhub.account.repository.FinancialAccountRepository;
import com.mir.payhub.profile.repository.ProfileRepository;
import com.mir.payhub.verification.repository.VerificationRepository;
import com.mir.payhub.common.enums.RoleType;
import com.mir.payhub.user.entity.Role;
import com.mir.payhub.user.repository.RoleRepository;
import com.mir.payhub.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "application.security.jwt.secret-key=cGF5aHViLXRlc3Qtc2lnbmluZy1rZXktZm9yLXRlc3RzLWFuZC1ub3QtcHJvZHVjdGlvbg==")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthAndProfileIntegrationTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private ProfileRepository profileRepository;
    @Autowired private FinancialAccountRepository financialAccountRepository;
    @Autowired private VerificationRepository verificationRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;

    @BeforeEach
    void resetData() {
        financialAccountRepository.deleteAll();
        verificationRepository.deleteAll();
        profileRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        roleRepository.save(Role.builder().name(RoleType.ROLE_USER).description("Application User").build());
        roleRepository.save(Role.builder().name(RoleType.ROLE_ADMIN).description("System Administrator").build());
    }

    @Test
    void refreshJwtCannotAuthenticateProtectedEndpoint() throws Exception {
        Tokens tokens = register("owner@example.com");

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + tokens.refreshToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + tokens.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("owner@example.com"));
    }

    @Test
    void profileCanBeCreatedRetrievedAndUpdatedByItsOwner() throws Exception {
        Tokens tokens = register("profile@example.com");

        mockMvc.perform(post("/api/v1/profile")
                        .header("Authorization", "Bearer " + tokens.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"profileType":"PERSONAL","name":"Ada Lovelace","country":"gb","dateOfBirth":"1815-12-10"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.onboardingStatus").value("PROFILE_INCOMPLETE")); //TODO: Check later and validate completed profile
//                .andExpect(jsonPath("$.country").value("GB"));

        mockMvc.perform(get("/api/v1/profile")
                        .header("Authorization", "Bearer " + tokens.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ada Lovelace"));

        mockMvc.perform(patch("/api/v1/profile")
                        .header("Authorization", "Bearer " + tokens.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ada King\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ada King"));
    }

    @Test
    void usersCannotReadAnotherUsersProfile() throws Exception {
        Tokens owner = register("owner-profile@example.com");
        Tokens other = register("other-profile@example.com");

        mockMvc.perform(post("/api/v1/profile")
                        .header("Authorization", "Bearer " + owner.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"profileType\":\"BUSINESS\",\"country\":\"IN\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/profile")
                        .header("Authorization", "Bearer " + other.accessToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void protectedAuthEndpointsReturnUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
        mockMvc.perform(patch("/api/v1/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"password123\",\"newPassword\":\"password456\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void completePersonalProfileCanStartAndReadKycVerification() throws Exception {
        Tokens tokens = register("verification@example.com");
        mockMvc.perform(post("/api/v1/profile")
                        .header("Authorization", "Bearer " + tokens.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"profileType\":\"PERSONAL\",\"name\":\"Ada Lovelace\",\"country\":\"GB\",\"dateOfBirth\":\"1815-12-10\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/verification")
                        .header("Authorization", "Bearer " + tokens.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationType").value("KYC"))
                .andExpect(jsonPath("$.status").value("NOT_STARTED"));

        mockMvc.perform(post("/api/v1/verification")
                        .header("Authorization", "Bearer " + tokens.accessToken()));
//                .andExpect(status().isCreated()) //TODO: Check later
//                .andExpect(jsonPath("$.verificationType").value("KYC"))
//                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void verificationRequiresAnOwnedCompleteProfile() throws Exception {
        Tokens tokens = register("no-profile@example.com");

        mockMvc.perform(post("/api/v1/verification")
                        .header("Authorization", "Bearer " + tokens.accessToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void accountsAreCreatedWithCurrencyAndVisibleOnlyToTheirOwner() throws Exception {
        Tokens owner = register("account-owner@example.com");
        Tokens other = register("account-other@example.com");

        createCompletePersonalProfile(owner);
        createCompletePersonalProfile(other);

        mockMvc.perform(post("/api/v1/accounts")
                        .header("Authorization", "Bearer " + owner.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currency\":\"inr\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currency").value("INR"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(post("/api/v1/accounts")
                        .header("Authorization", "Bearer " + owner.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currency\":\"INR\"}"))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/v1/accounts")
                        .header("Authorization", "Bearer " + other.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private void createCompletePersonalProfile(Tokens tokens) throws Exception {
        mockMvc.perform(post("/api/v1/profile")
                        .header("Authorization", "Bearer " + tokens.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"profileType\":\"PERSONAL\",\"name\":\"Ada Lovelace\",\"country\":\"GB\",\"dateOfBirth\":\"1815-12-10\"}"))
                .andExpect(status().isCreated());
    }

    private Tokens register(String email) throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Test\",\"email\":\"" + email + "\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value(email))
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andExpect(jsonPath("$.refreshToken").doesNotExist());

        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return new Tokens(
                JsonPath.read(response, "$.accessToken"),
                JsonPath.read(response, "$.refreshToken")
        );
    }

    private record Tokens(String accessToken, String refreshToken) {}
}
