package sy.util;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class KgNode {

    private String id;
    private String name;
    private String label;
    private String content;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public String getContent() {
        return content;
    }


    public static class Builder {
        private String id;
        private final String name;
        private final String label;

        private String content;

        public Builder(String name, String label) {
            this.name = name;
            this.label = label;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public KgNode build() {
            return new KgNode(this);
        }
    }

    private KgNode(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.label = builder.label;
        this.content = builder.content;
    }

    @Override
    public String toString() {
        return this.label + " -> " + this.id + " -> " + this.name;
    }

}

