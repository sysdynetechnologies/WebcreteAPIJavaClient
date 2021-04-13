
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.Service;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.concretego.api.WebcreteAPI;
import com.concretego.api.WebcreteAPISoap;






public class TestConcreteGoClient {

	public static void main(String[] args) {
		try {
		
		WebcreteAPI api = new WebcreteAPI(new URL("http://?????.api.concretego.com/webcreteapi.asmx"));
		
		ConcreteGoHandlerResolver cghr = new ConcreteGoHandlerResolver();
		
		((Service)api).setHandlerResolver(cghr);

		
		WebcreteAPISoap apiSoap = api.getWebcreteAPISoap();
		

		
		String username = "username" ;
		String password = "password" ;
		
	
		String ticketHeader = apiSoap.getPublicKey( "AppID" , "APIKEY");

		
		System.out.println(ticketHeader);
			
				
				RSAPublicKey pubKey = ReadXMLKey(new ByteArrayInputStream(ticketHeader.getBytes()));

				Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
				cipher.init(Cipher.ENCRYPT_MODE, pubKey);

				byte[] plainBytes = password.getBytes("UTF-16LE");
				byte[] cipherData = cipher.doFinal(plainBytes);
				byte[] encryptedStringBase64 = Base64.getEncoder().encode(cipherData);
			
				System.out.println("encrypted: " + new String(encryptedStringBase64));
				
				boolean success = apiSoap.login(username, cipherData);
				
				System.out.println(success);
				
				if (success) {
					getCustomers(apiSoap);
					
				}
				
				apiSoap.logout();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
	}
	
	
	private static void getCustomers(WebcreteAPISoap apiSoap) throws Exception {
		String version = "<?webcretexml version = \"1.0\" ?>\r\n" + 
				"	<WebcreteXML>\r\n" + 
				"	<WebcreteXMLMsgsRq>\r\n" + 
				"	<CustomerQueryRq>\r\n" + 
				"	</CustomerQueryRq>\r\n" + 
				"	</WebcreteXMLMsgsRq>\r\n" + 
				"	</WebcreteXML>";
		String response = apiSoap.processRequest(version);
		
		System.out.println(response);
		DocumentBuilderFactory factory =     DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(response.getBytes()));
        NodeList list = document.getElementsByTagName("CustomerRet");
        
        
        for (int i = 0; i < list.getLength(); i++) {
        	Element eElement = (Element) list.item(i);
        	System.out.print(formatColumnValue(eElement.getElementsByTagName("Name").item(0).getTextContent()));

        	Element deliveryAdress = (Element) eElement.getElementsByTagName("Address").item(0);
        	

        	String address = getValueFromElement(deliveryAdress, "Addr1");
        	String tempAddress = getValueFromElement(deliveryAdress, "Addr2");
        	if (StringUtils.isNotEmpty(tempAddress)) {
        		address += ", " + tempAddress;
        	}
        	tempAddress = getValueFromElement(deliveryAdress, "Addr3");
        	if (StringUtils.isNotEmpty(tempAddress)) {
        		address += ", " + tempAddress;
        	}
        		
    		System.out.print(formatColumnValue(address));
    		System.out.print(formatColumnValue(getValueFromElement(deliveryAdress, "City")));
    		System.out.print(formatColumnValue(getValueFromElement(deliveryAdress, "State")));
    		System.out.print(formatColumnValue(getValueFromElement(deliveryAdress, "PostalCode")));
    		System.out.print(formatColumnValue(getValueFromElement(eElement, "Contact")));
    		System.out.println(formatColumnValue(getValueFromElement(eElement, "Phone")));
        }
	}
	
	
	
	
	private static String getValueFromElement(Element element, String name) {
		return element.getElementsByTagName(name).item(0).getTextContent();
	}
	
	
	
	private static RSAPublicKey ReadXMLKey(InputStream is) throws Exception
	{

	        DocumentBuilderFactory factory =     DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document document = builder.parse(is);
	        byte[] modBytes = GetBytesFromElement(document, "Modulus");
	        byte[] expBytes = GetBytesFromElement(document, "Exponent");
	        RSAPublicKeySpec rsaKeyspec = new RSAPublicKeySpec(new BigInteger(1, modBytes), new BigInteger(1, expBytes));
	        RSAPublicKey key = (RSAPublicKey)KeyFactory.getInstance("RSA").generatePublic(rsaKeyspec);

	        return key;
	         }

	private static byte[] GetBytesFromElement(Document doc, String tag) throws IOException
	{
		
	    NodeList list = doc.getElementsByTagName(tag);
	    byte[] results = null;
	    if (list.getLength() == 1)
	    {
	        Element item = (Element)list.item(0);
	        Text text = (Text)item.getFirstChild();
	        results = Base64.getDecoder().decode(text.getNodeValue().trim());
	    }
	    return results;
	}
	
	private static String formatColumnValue(String value) {
		return value == null ? "," : "\"" + value + "\",";
	
	}

}

