package lending.platform.lendingplatform.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

//@WebMvcTest
class LoanControllerTest {
    //toDo Check on how to resolve the dependacies like the WebMvcTest which has now moved to springdocs
    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }
}