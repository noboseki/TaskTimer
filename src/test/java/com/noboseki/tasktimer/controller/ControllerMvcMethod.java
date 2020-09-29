package com.noboseki.tasktimer.controller;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ControllerMvcMethod {

    private MockMvc mockMvc;

    public ControllerMvcMethod(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public MvcResult createCorrect(String url, String jsonObject) throws Exception {
        return mockMvc.perform(post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonObject))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success",is(true)))
                .andExpect(jsonPath("message",is("Test Ok"))).andReturn();
    }

    public MvcResult createValid(String url, String jsonObject) throws Exception {
        return mockMvc.perform(post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonObject))
                .andExpect(status().is4xxClientError()).andReturn();
    }

    public MvcResult updateCorrect(String url, String jsonObject) throws Exception {
        return mockMvc.perform(put(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonObject))
                    .andExpect(status().isOk())
                .andExpect(jsonPath("success",is(true)))
                .andExpect(jsonPath("message",is("Test Ok"))).andReturn();
    }

    public MvcResult updateValid(String url, String jsonObject) throws Exception {
        return mockMvc.perform(put(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(jsonObject))
                .andExpect(status().is4xxClientError()).andReturn();
    }

    public MvcResult deleteCorrect(String url) throws Exception {
        return mockMvc.perform(delete(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success",is(true)))
                .andExpect(jsonPath("message",is("Test Ok"))).andReturn();
    }

    public MvcResult deleteValid(String url) throws Exception {
        return mockMvc.perform(delete(url))
                .andExpect(status().is4xxClientError()).andReturn();
    }

    public MvcResult getValid(String url) throws Exception {
        return mockMvc.perform(get(url))
                .andExpect(status().is4xxClientError()).andReturn();
    }
}
