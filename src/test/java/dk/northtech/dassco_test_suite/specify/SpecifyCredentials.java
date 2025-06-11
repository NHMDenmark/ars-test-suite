package dk.northtech.dassco_test_suite.specify;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpecifyCredentials {
    
    private String specifyId;
    private String specifySecret;
    private int collectionId;
    private String specifyUrl;

    public SpecifyCredentials(int collectionId){
        
        ApplicationContext context = new AnnotationConfigApplicationContext(CredentialsBean.class);
		
		this.specifyId = context.getBean("beanSpecifyId", String.class);
		this.specifySecret = context.getBean("beanSpecifySecret", String.class);
		this.specifyUrl = context.getBean("beanSpecifyUrl", String.class);
        this.collectionId = collectionId;
    }

    public String getSpecifyId(){
        return this.specifyId;
    }

    
    public String getSpecifySecret(){
        return this.specifySecret;
    }   

    
    public int getCollectionId(){
        return this.collectionId;
    }

    public String getSpecifyUrl(){
        return this.specifyUrl;
    }
}
