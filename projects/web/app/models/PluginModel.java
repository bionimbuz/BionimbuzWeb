package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
@Table(name = "tb_plugin")
public class PluginModel extends GenericModel {

    @Id
    @GeneratedValue
    private Long id;
    @Required
    @MaxSize(400)
    private String url;
    @Required
    @MaxSize(100)
    private String name;
    @MaxSize(10)
    private String pluginVersion;
    @MaxSize(100)
    private String cloudType;
}
