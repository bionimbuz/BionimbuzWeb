package models;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_space_file")
public class SpaceFileModel extends GenericModel {

    private static SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyyMMddHHmmssSSS");

    @Id
    @GeneratedValue
    private Long id;
    @Required
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private SpaceModel space;
    @Required
    @MaxSize(100)
    private String virtualName;
    @Required
    @MaxSize(100)
    private String name;
    @MaxSize(200)
    private String publicUrl;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public SpaceModel getSpace() {
        return space;
    }
    public void setSpace(SpaceModel space) {
        this.space = space;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getVirtualName() {
        return virtualName;
    }
    public void setVirtualName(String virtualName) {
        this.virtualName = virtualName;
    }
    public String getPublicUrl() {
        return publicUrl;
    }
    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public static String generateVirtualName(final String fileName) {
        String virtualName = DATE_FORMAT.format(new Date());
        virtualName += "_" + Math.abs(fileName.hashCode());
        return virtualName;
    }
}
