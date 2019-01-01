package com.wisdom.separate.enumClass;


/**
 * @author:youb
 * @date:2018/8/13
 */
public enum ProcessStatusEnum {
    Finish((short) 2),
    ProcessIng((short) 1),
    WaitPrcess((short) 0),
    Failure((short)4);

    private short value;

    private ProcessStatusEnum(short value) {
        this.value = value;
    }

    public short getValue() {
        return this.value;
    }
}
