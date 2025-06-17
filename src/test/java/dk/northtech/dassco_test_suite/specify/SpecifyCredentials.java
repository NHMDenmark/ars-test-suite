package dk.northtech.dassco_test_suite.specify;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpecifyCredentials {
    
    private String specifyId;
    private String specifySecret;
    private String specifyCollectionId;
    private String specifyUrl;

    public SpecifyCredentials(){
        
        ApplicationContext context = new AnnotationConfigApplicationContext(CredentialsBean.class);
		
		this.specifyId = context.getBean("beanSpecifyId", String.class);
		this.specifySecret = context.getBean("beanSpecifySecret", String.class);
		this.specifyUrl = context.getBean("beanSpecifyUrl", String.class);
        this.specifyCollectionId = context.getBean("beanSpecifyCollectionId", String.class);

    }

    public String getSpecifyId(){
        return this.specifyId;
    }
    
    public String getSpecifySecret(){
        return this.specifySecret;
    }   

    public String getSpecifyUrl(){
        return this.specifyUrl;
    }

    public String getSpecifyCollectionId(){
        return this.specifyCollectionId;
    }
}
