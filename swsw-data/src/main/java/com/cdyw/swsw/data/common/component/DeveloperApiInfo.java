package com.cdyw.swsw.data.common.component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jovi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeveloperApiInfo {

    private String name;

    private String email;

    private String role;
}
