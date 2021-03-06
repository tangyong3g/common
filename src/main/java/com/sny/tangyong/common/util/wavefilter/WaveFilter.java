package com.sny.tangyong.common.util.wavefilter;


/**
 * @author tyler.tang
 */
public class WaveFilter {

    /**
     * 限幅滤波
     * <p>
     * B、优点：
     * 能有效克服因偶然因素引起的脉冲干扰
     * C、缺点
     * 无法抑制那种周期性的干扰
     * 平滑度差
     */
    public static class LimiteArrangeFilter implements IWaveFilter {

        float mLastValue = 0.0f;
        public float mLimit = 10;

        @Override
        public float filter(float source) {
            float result = 0.0f;

            if (Math.abs(source - mLastValue) > mLimit && mLastValue != 0) {
                result = mLastValue;
            } else {
                result = source;
            }
            mLastValue = result;

            return result;
        }
    }


    /**
     * 中位数滤波
     * <p>
     * 存储一个缓冲区,排序然后取中间的数
     * <p>
     * TODO -- 未完待续
     */
    public static class MedianFilter implements IWaveFilter {

        //缓冲区大小
        public int bufferCount = 10;
        public float[] bufferArray = new float[bufferCount];
        int currentIndex = 0;

        @Override
        public float filter(float source) {
            return 0;
        }

        private void push(float value) {

            if (currentIndex >= (bufferCount - 1)) {
                //元素满了的时候
                removeToPreIndex(value);
            } else {
                //未满的时候
                bufferArray[currentIndex++] = value;
            }
        }

        /**
         * 向前面移动一位
         */
        private void removeToPreIndex(float value) {

            //所有元素往前移动一位,参数 value 放到顶上
            for (int i = 0; i <= bufferCount - 1; i++) {
                if (i == bufferCount - 1) {
                    bufferArray[bufferCount - 1] = value;
                } else {
                    bufferArray[i] = bufferArray[i + 1];
                }
            }
        }

    }


    /**
     * 算术平均滤波法
     * <p>
     * B、优点：
     * 适用于对一般具有随机干扰的信号进行滤波
     * 这样信号的特点是有一个平均值，信号在某一数值范围附近上下波动
     * C、缺点：
     * 对于测量速度较慢或要求数据计算速度较快的实时控制不适用
     * 比较浪费RAM
     */
    public static class CalcAvgFilter implements IWaveFilter {

        @Override
        public float filter(float source) {
            push(source);
            return calcArrAvg();
        }

        //缓冲区大小
        public int bufferCount = 10;
        public float[] bufferArray = new float[bufferCount];
        int currentIndex = 0;


        private void push(float value) {

            if (currentIndex >= (bufferCount - 1)) {
                //元素满了的时候
                removeToPreIndex(value);
            } else {
                //未满的时候
                bufferArray[currentIndex++] = value;
            }
        }

        private float calcArrAvg() {
            float result = 0;
            for (int i = 0; i <= currentIndex; i++) {
                result += bufferArray[i];
            }
            return result / (currentIndex + 1);
        }

        /**
         * 向前面移动一位
         */
        private void removeToPreIndex(float value) {

            //所有元素往前移动一位,参数 value 放到顶上
            for (int i = 0; i <= bufferCount - 1; i++) {
                if (i == bufferCount - 1) {
                    bufferArray[bufferCount - 1] = value;
                } else {
                    bufferArray[i] = bufferArray[i + 1];
                }
            }
        }
    }


    /**
     * 递推平均滤波法（又称滑动平均滤波法）
     * <p>
     * B、优点：
     * 对周期性干扰有良好的抑制作用，平滑度高
     * 适用于高频振荡的系统
     * C、缺点：
     * 灵敏度低
     * 对偶然出现的脉冲性干扰的抑制作用较差
     * 不易消除由于脉冲干扰所引起的采样值偏差
     * 不适用于脉冲干扰比较严重的场合
     * 比较浪费RAM
     */
    public static class PullAvgFilter implements IWaveFilter {

        @Override
        public float filter(float source) {
            push(source);
            return calcArrAvg();
        }

        //缓冲区大小
        public int bufferCount = 10;
        public float[] bufferArray = new float[bufferCount];
        int currentIndex = 0;


        private void push(float value) {

            if (currentIndex >= (bufferCount - 1)) {
                //元素满了的时候
                removeToPreIndex(value);
            } else {
                //未满的时候
                bufferArray[currentIndex++] = value;
            }
        }

        private float calcArrAvg() {
            float result = 0;
            for (int i = 0; i <= currentIndex; i++) {
                result += bufferArray[i];
            }
            return result / (currentIndex + 1);
        }

        /**
         * 向前面移动一位
         */
        private void removeToPreIndex(float value) {

            //所有元素往前移动一位,参数 value 放到顶上
            for (int i = 0; i <= bufferCount - 1; i++) {
                if (i == bufferCount - 1) {
                    bufferArray[bufferCount - 1] = value;
                } else {
                    bufferArray[i] = bufferArray[i + 1];
                }
            }
        }
    }


    /**
     * 中位值平均滤波法（又称防脉冲干扰平均滤波法）
     * A、方法：
     * 相当于“中位值滤波法”+“算术平均滤波法”
     * 连续采样N个数据，去掉一个最大值和一个最小值
     * 然后计算N-2个数据的算术平均值
     * N值的选取：3~14
     * B、优点：
     * 融合了两种滤波法的优点
     * 对于偶然出现的脉冲性干扰，可消除由于脉冲干扰所引起的采样值偏差
     * C、缺点：
     * 测量速度较慢，和算术平均滤波法一样
     * 比较浪费RAM
     */
    public static class MiddleValueFilter implements IWaveFilter {

        @Override
        public float filter(float source) {
            push(source);
            return calcArrAvg();
        }

        //缓冲区大小
        public int bufferCount = 10;
        public float[] bufferArray = new float[bufferCount];
        int currentIndex = 0;


        private void push(float value) {

            if (currentIndex >= (bufferCount - 1)) {
                //元素满了的时候
                removeToPreIndex(value);
            } else {
                //未满的时候
                bufferArray[currentIndex++] = value;
            }
        }

        private float calcArrAvg() {

            float result = 0.0f;
            //排序
            if (currentIndex > 1) {

                for (int i = 0; i <= currentIndex; i++) {

                    for (int j = 0; j <= currentIndex - i - 1; j++) {

                        if (bufferArray[j] > bufferArray[j + 1]) {

                            float temp = bufferArray[j];
                            bufferArray[j] = bufferArray[j + 1];
                            bufferArray[j + 1] = temp;

                        }
                    }
                }
            }

            //去掉首尾元素
            for (int k = 1; k < currentIndex; k++) {
                result += bufferArray[currentIndex];
            }

            //取平均值
            result /= (currentIndex - 1);

            return result;

        }


        /**
         * 向前面移动一位
         */
        private void removeToPreIndex(float value) {

            //所有元素往前移动一位,参数 value 放到顶上
            for (int i = 0; i <= bufferCount - 1; i++) {
                if (i == bufferCount - 1) {
                    bufferArray[bufferCount - 1] = value;
                } else {
                    bufferArray[i] = bufferArray[i + 1];
                }
            }
        }
    }


    /**
     * 6、限幅平均滤波法
     * A、方法：
     * 相当于“限幅滤波法”+“递推平均滤波法”
     * 每次采样到的新数据先进行限幅处理，
     * 再送入队列进行递推平均滤波处理
     * B、优点：
     * 融合了两种滤波法的优点
     * 对于偶然出现的脉冲性干扰，可消除由于脉冲干扰所引起的采样值偏差
     * C、缺点：
     * 比较浪费RAM
     */
    public static class LimitePullAvgFilter implements IWaveFilter {

        @Override
        public float filter(float source) {
            push(source);
            return calcArrAvg();
        }

        //缓冲区大小
        public int bufferCount = 10;
        public float[] bufferArray = new float[bufferCount];
        int currentIndex = 0;


        private void push(float value) {

            LimiteArrangeFilter filter = new WaveFilter.LimiteArrangeFilter();
            value = filter.filter(value);

            if (currentIndex >= (bufferCount - 1)) {
                //元素满了的时候
                removeToPreIndex(value);
            } else {
                //未满的时候
                bufferArray[currentIndex++] = value;
            }
        }

        private float calcArrAvg() {
            float result = 0;
            for (int i = 0; i <= currentIndex; i++) {
                result += bufferArray[i];
            }
            return result / (currentIndex + 1);
        }

        /**
         * 向前面移动一位
         */
        private void removeToPreIndex(float value) {

            //所有元素往前移动一位,参数 value 放到顶上
            for (int i = 0; i <= bufferCount - 1; i++) {
                if (i == bufferCount - 1) {
                    bufferArray[bufferCount - 1] = value;
                } else {
                    bufferArray[i] = bufferArray[i + 1];
                }
            }
        }
    }


    /**
     * 一阶滞后滤波法
     * A、方法：
     * 取a=0~1
     * 本次滤波结果=（1-a）*本次采样值+a*上次滤波结果
     * B、优点：
     * 对周期性干扰具有良好的抑制作用
     * 适用于波动频率较高的场合
     * C、缺点：
     * 相位滞后，灵敏度低
     * 滞后程度取决于a值大小
     * 不能消除滤波频率高于采样频率的1/2的干扰信号
     */
    public static class FirstDelayFilter implements IWaveFilter {

        float mLastValue = 0.0f;
        float filter = 0.15f;

        @Override
        public float filter(float source) {
            float result = mLastValue * (1 - filter) + filter * source;
            mLastValue = result;
            return result;
        }
    }


    /**
     * TODO 未完成待续
     * 加权递推平均滤波法
     * A、方法：
     * 是对递推平均滤波法的改进，即不同时刻的数据加以不同的权
     * 通常是，越接近现时刻的数据，权取得越大。
     * 给予新采样值的权系数越大，则灵敏度越高，但信号平滑度越低
     * B、优点：
     * 适用于有较大纯滞后时间常数的对象
     * 和采样周期较短的系统
     * C、缺点：
     * 对于纯滞后时间常数较小，采样周期较长，变化缓慢的信号
     * 不能迅速反应系统当前所受干扰的严重程度，滤波效果差
     */

    public static class PlusAvgFilter implements IWaveFilter {

        private int bufferCount = 12;
        private int coeArray[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};


        @Override
        public float filter(float source) {
            return 0;
        }
    }


    /**
     * TODO  未完
     * A、方法：
     * <p>
     * 设置一个滤波计数器
     * <p>
     * 将每次采样值与当前有效值比较：
     * <p>
     * 如果采样值＝当前有效值，则计数器清零
     * <p>
     * 如果采样值<>当前有效值，则计数器+1，并判断计数器是否>=上限N(溢出)
     * <p>
     * 如果计数器溢出,则将本次值替换当前有效值,并清计数器
     * <p>
     * B、优点：
     * <p>
     * 对于变化缓慢的被测参数有较好的滤波效果,
     * <p>
     * 可避免在临界值附近控制器的反复开/关跳动或显示器上数值抖动
     * <p>
     * C、缺点：
     * <p>
     * 对于快速变化的参数不宜
     * <p>
     * 如果在计数器溢出的那一次采样到的值恰好是干扰值,则会将干扰值当作有效值导入系统
     * <p>
     * <p>
     * note: 消抖滤波感觉像采样一样.
     */
    public static class RemoveShakeFilter implements IWaveFilter {

        int i = 0;
        int filterCount = 12;
        float value = 0.0f;

        @Override
        public float filter(float source) {

            if (value != source) {
                i++;
                if (i > filterCount) {
                    value = source;
                }
            } else {
                i = 0;
            }

            return value;
        }
    }

}
