package com.cdyw.swsw.common.domain.dto.fusion;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author jovi
 */
@Data
public class Fusion12hLonLatArray implements Serializable {

    private List<Double> lon;

    private List<Double> lat;

}