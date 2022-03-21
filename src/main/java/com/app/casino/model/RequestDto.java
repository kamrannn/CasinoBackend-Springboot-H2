package com.app.casino.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RequestDto {
    @NotBlank
    @NotEmpty
    public String username;
    @NotBlank
    @NotEmpty
    public String topSecret;
}
