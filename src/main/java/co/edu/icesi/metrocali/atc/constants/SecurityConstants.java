package co.edu.icesi.metrocali.atc.constants;

public class SecurityConstants {
	
	public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 5*60*60;
    public static final String SIGNING_KEY = "devglan123r";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String AUTHORITIES_KEY = "scopes";
	public static final String Controller_Login_Url = "/atc/sign_in/controllers";
	public static final String Omega_Login_Url = "/atc/sign_in/omegas";
	public static final String Admin_Login_Url = "/atc/sign_in/admins";
}
