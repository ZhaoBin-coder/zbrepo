package pojo;

public class Artical {
    private String cid;
    private String catalog;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    @Override
    public String toString() {
        return "Artical{" +
                "cid='" + cid + '\'' +
                ", catalog='" + catalog + '\'' +
                '}';
    }
}
