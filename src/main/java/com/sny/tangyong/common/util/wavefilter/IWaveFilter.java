package com.sny.tangyong.common.util.wavefilter;


/**
 * 滤波
 */
public interface IWaveFilter {

    /**
     * @param source 输入的滤波原数据
     * @return
     */
    float filter(float source);
}
