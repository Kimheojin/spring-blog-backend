package HeoJin.demoBlog.global.exception.refactor;

public enum BusinessErrorCode {
    // 비지니스 조건 실패
    INVALID_REQUEST(400, "INVALID_REQUEST"),
    // 중복 생성
    DUPLICATE_RESOURCE(409, "DUPLICATE_RESOURCE"),
    // 접근 가능 but 없는 것 처럼
    RESOURCE_NOT_FOUND(404, "RESOURCE_NOT_FOUND"),
    // 금지
    FORBIDDEN_ACTION(403, "FORBIDDEN_ACTION");

    private final int status;
    private final String code;

    BusinessErrorCode(int status, String code) {
        this.status = status;
        this.code = code;
    }

    public int getStatus() { return status; }
    public String getCode() { return code; }


}
