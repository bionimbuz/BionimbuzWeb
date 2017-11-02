package app.models;

public class CredentialModel <Model>{
    private String credential;
    private Model model;
    
    @SuppressWarnings("unused") // Reflection purposes     
    private CredentialModel() {   
    }    
    public CredentialModel(String credential) {
        this.credential = credential;
    }
    
    public String getCredential() {
        return credential;
    }
    public void setCredential(String credential) {
        this.credential = credential;
    }
    public Model getModel() {
        return model;
    }
    public void setModel(Model model) {
        this.model = model;
    }
}
