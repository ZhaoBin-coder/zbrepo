package pojo;

public class Two {
    private String cid;
    private String url;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Two{" +
                "cid='" + cid + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
