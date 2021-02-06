package ru.cti.omiliachatbot.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Prompt {

    @JsonProperty("content")
    private String content;

    @JsonProperty("masked_content")
    private String masked_content;

    @JsonProperty("voice")
    private String voice;

    @JsonProperty("visualize")
    private String visualize;

    @JsonProperty("bargein")
    private String bargein;

    @JsonIgnore
    @JsonProperty("prompt_urls")
    private String prompt_urls;


    public String getContent() {
        return content;
    }

    public String getMasked_content() {
        return masked_content;
    }

    public String getVoice() {
        return voice;
    }

    public String getVisualize() {
        return visualize;
    }

    public String getBargein() {
        return bargein;
    }

    public String getPrompt_urls() {
        return prompt_urls;
    }
}
