package com.wisdom.separate.enumClass;

/**
 * @author:youben
 * @date:2018/5/9
 * @desc:数据迁移枚举，用来控制迁移数据优先级
 */
public enum SyncCleanDataPriorityEunum {
    HigthA(7),
    HigthB(6),
    HigthC(5),
    NormalA(4),
    NormalB(3),
    NormalC(2),
    LowA(1);

    private int value;

    SyncCleanDataPriorityEunum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
