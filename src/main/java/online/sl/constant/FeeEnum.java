package online.sl.constant;

public enum FeeEnum {
    BERTH_FEE("床位费"),
    BERTH_REVOCATION_FEE("撤销预约费率")
    ;


    private String name;

    FeeEnum(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
