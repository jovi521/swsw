package com.cdyw.swsw.common.domain.ao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 产品类型
 *
 * @author jovi
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ProductEnum {
    /**
     * 体扫
     */
    VOL(2),
    /**
     * 基本反射率
     */
    Z(3),
    /**
     * 径向速度
     */
    V(4),
    /**
     * 基本谱宽
     */
    W(5),
    /**
     * 回波顶高
     */
    ET(6),
    /**
     * 回波底高
     */
    EB(7),
    /**
     * 等高平面显示
     */
    CAPPI(8),
    /**
     * 反射率垂直切割
     */
    RCS(9),
    /**
     * 组合反射率
     */
    MAXREF(10),
    /**
     * 垂直液态含水量
     */
    VIL(25),
    /**
     * 暴雨回波识别
     */
    SEI(50),
    /**
     * 下击暴流识别
     */
    DDPD(51),
    /**
     * 未滤波反射率
     */
    UNZ(52),
    /**
     * 大气波导
     */
    RFC(53),
    /**
     * 冰雹指数
     */
    HI(55),
    /**
     * 雨强
     */
    EXTRARZ(203),
    /**
     * 垂直液态含水量
     */
    EXTRAVIL(204),
    /**
     * 风场
     */
    EXTRAIVAP(205),
    /**
     * 冰雹识别
     */
    EXTRAHS(206),
    /**
     * 大风识别
     */
    EXTRAVW(207),
    /**
     * 暴雨识别
     */
    EXTRARS(208),
    /**
     * 分钟降水量
     */
    EXTRAVSP(209),
    /**
     * 闪电定位
     */
    LIGHT(300),
    /**
     * 地面数据
     */
    GROUND(301),
    /**
     * 近1小时降水量
     */
    ONEHOURSERAIN(302),
    /**
     * 近10分钟降水量
     */
    TENMINRAIN(303),
    /**
     * 2分钟平均风速
     */
    TWOMINWIND_S(304),
    /**
     * 10分钟平均风速
     */
    TENMINWIND_S(305),
    /**
     * 2分钟平均风向
     */
    TWOMINWIND_D(306),
    /**
     * 10分钟平均风向
     */
    TENMINWIND_D(307),
    /**
     * 风廓线雷达状态数据
     */
    MONITOR(500),
    /**
     * 风廓线雷达功率谱数据
     */
    WNDFFT(501),
    /**
     * 风廓线雷达功率谱实时数据
     */
    ROBS(502),
    /**
     * 风廓线雷达功率谱半小时数据
     */
    HOBS(503),
    /**
     * 风廓线雷达功率谱1小时数据
     */
    OOBS(504),
    /**
     * 风廓线雷达径向数据
     */
    WNDRAD(505),
    /**
     * 自动站相对湿度
     */
    RHU(601),
    /**
     * 自动站气压
     */
    PRS(602),
    /**
     * 自动站气温
     */
    TEM(603),
    /**
     * 自动站最大风速
     */
    WIN_S_MAX(604),
    /**
     * 自动站最大风速的风向
     */
    WIN_D_S_MAX(604),
    /**
     * 自动站过去一小时降水量
     */
    PRE_1H(407),
    /**
     * 自动站过去10分钟降水量
     */
    PRE_10MIN(607),
    /**
     * 自动站气温色斑图
     */
    TEM_CF(608),
    /**
     * 自动站气压色斑图
     */
    PRS_CF(609),
    /**
     * 自动站相对湿度色斑图
     */
    RHU_CF(610),
    /**
     * 自动站过去一小时降水量色斑图
     */
    PRE_1H_CF(611),
    /**
     * 自动站过去10分钟降水量色斑图
     */
    PRE_10MIN_CF(612),
    /**
     * 自动站气温等值线
     */
    TEM_CONTOUR(613),
    /**
     * 自动站气压等值线
     */
    PRS_CONTOUR(614),
    /**
     * 自动站相对湿度等值线
     */
    RHU_CONTOUR(615),
    /**
     * 自动站过去一小时降水量等值线
     */
    PRE_1H_CONTOUR(616),
    /**
     * 自动站过去10分钟降水量等值线
     */
    PRE_10MIN_CONTOUR(617),
    /**
     * 雷达外推产品
     */
    FCST(701),
    /**
     * 能见度
     */
    VIS(702),
    /**
     * 红外通道
     */
    IR1(801),
    /**
     * 红外分裂窗通道
     */
    IR2(802),
    /**
     * 水汽通道
     */
    IR3(803),
    /**
     * 中红外通道
     */
    IR4(804),
    /**
     * 可见光通道
     */
    VIS_CHANNEL(805),
    /**
     * FY4A通道1
     */
    CHANNEL01(806),
    /**
     * FY4A通道2
     */
    CHANNEL02(807),
    /**
     * FY4A通道3
     */
    CHANNEL03(808),
    /**
     * FY4A通道4
     */
    CHANNEL04(809),
    /**
     * FY4A通道5
     */
    CHANNEL05(810),
    /**
     * FY4A通道6
     */
    CHANNEL06(811),
    /**
     * FY4A通道7
     */
    CHANNEL07(812),
    /**
     * FY4A通道8
     */
    CHANNEL08(813),
    /**
     * FY4A通道9
     */
    CHANNEL09(814),
    /**
     * FY4A通道10
     */
    CHANNEL10(815),
    /**
     * FY4A通道11
     */
    CHANNEL11(816),
    /**
     * FY4A通道12
     */
    CHANNEL12(817),
    /**
     * FY4A通道13
     */
    CHANNEL13(818),
    /**
     * FY4A通道14
     */
    CHANNEL14(819),
    /**
     * FUSION12H_TEM 2501
     */
    FUSION12H_TEM(2501),
    /**
     * FUSION12H_WIND 2502
     */
    FUSION12H_WIND(2502),
    /**
     * FUSION12H_RHU 2503
     */
    FUSION12H_RHU(2503),
    /**
     * FUSION12H_PRS 2504
     */
    FUSION12H_PRS(2504),
    /**
     * FUSION12H_RAIN 2505
     */
    FUSION12H_RAIN(2505),
    /**
     * FUSION12H_CLOUD 2506
     */
    FUSION12H_CLOUD(2506);

    private Integer value;
}
