package be.howest.adria.infrastructure.webapi.shared;

public final class ProblemDetails {
    private final String type;
    private final String title;
    private final int status;
    private final String detail;
    private final String instance;

    private ProblemDetails(Builder builder) {
        this.type = builder.type;
        this.title = builder.title;
        this.status = builder.status;
        this.detail = builder.detail;
        this.instance = builder.instance;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public String getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "ProblemDetails{" +
                "type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", detail='" + detail + '\'' +
                ", instance='" + instance + '\'' +
                '}';
    }

    public static class Builder {
        private String type = "";
        private String title = "";
        private int status;
        private String detail = "";
        private String instance = "";

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setStatus(int status) {
            this.status = status;
            return this;
        }

        public Builder setDetail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder setInstance(String instance) {
            this.instance = instance;
            return this;
        }

        public ProblemDetails build() {
            return new ProblemDetails(this);
        }
    }
}
