package com.example.kedamall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.kedamall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000116673340";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDrQAWZCn01tXEt3CG+pO2uYaomXWyWh5GcODxHlK4zph5nPj97TgBy6OD/1jMQjFSwoE2VvWIAsixClj9kAMO0H3sqJIa3vCv1dufzu+yWmExhu8gyxjR0Ql/2UkHFba2yMGT3QWLcPeLfMjHEejZqYllkmLTOzmh6QpasgW//5X/VcOh5A+B47zfmGxFXmQOSy6aSPoYYttikPIozO7WTAjcw0wVOZuHHQcmCgwnnXmCzG+mzXk4pbwP8RsybfRP/QpQUJKZG6I8vW2HG/VLDkGGh4ZUMU9xYCPSl0pbLpjKxNP9MDetw9B1dnQlKU+h68spqnPOHBcawiVw7NWN7AgMBAAECggEAROt8uFlaVWHSxpy3RiMLfwsdzh+QsIkHH71Z656jan5Aaf7Smk0/1GplgI5AZ5i1Nwm/sR7Kl2mbjeZ2q9WNgDe43Z/V1/5I4qDzU3S622PC/N8Gx9lJhMXtVzzCCBUZzhpfehCt5dg7nygVbHphdslZZx9V2UCfWzIm8mL5bP4/oa4yFoPqQJhg7tXxfjv4M20bfTazbXX4IciDaPpiWtiO06wYdaP1LTd1H9AXwpZJEz5nX8usQ4Lcco0YIedJl5NUgIpjuDlgJ4stwBBEeq0Ea33e4xot2HS94WmTtI6seKdClER/Q8EmzG51jNtKB6tAJE4WaniXzzyjy4AwAQKBgQD6zu8sEci5HLMkvgC7EkcpArSjmaWX+AwDEWvIFN14nVbJhwmg8lGm7ZwOk2oUZF6aTAQaF4KQ+vC+wYtcXaiav376obyUhPBkNK10J3anl8AWfI2M8ZgAiAaRpQl8um0rpS9YOUCSNsowlDv8Be7Jr3T/B1sVV1/IUuGc6A/ISQKBgQDwHqR1g4Y8oixEjcVtCq/ZE9vux7cAazVtJm77fEOiOSabJ0tdFU3VExQ0pn6eOP+emHyTeQtdZ+Q+iz61lpWZsN8r9I38Y7b9UEnN5mEPt/QlRblwn/I++jb+3iBJc8BEY6b/BsLZHf+LU1XunBBVy6JiOGahstyTmEM6GKP1owKBgQDbhN1/k/08UCtX4FlhYwkXkv+qJaPzVC6nTMsqb2C83fGFKAU8cnsXAOdTNqFlQcgvgpMghy1HWev2+g3RBUS6VRt9oKwezhBdlGGwxY2r+D0vMUrvbtGcvXIKbgnUupMs3UKIFGw4zW3AgS3TUxoCOujLApQtJLF381r264JMyQKBgB0keu1QQzCEtj6zvixXybPc9yppzsocOOXAbmPTVxoPOaJHCJyJg2LC6Abj1iY54LxM4YG8hSaW/1qf58J+PjXjA5MAIWNqbsEbx3lX5pvTxavoZHNI1kvSbm5MYzrG86TZO2FkXq2fOG6edKV7B3iCOHkXQ02IucCoEDCy4ONlAoGBALkUM/RAn4rrNcoITyRi86/axxYKPbkQIWi387L9I3cisVWRcvoBxIhNzy7NVA/ddL1zcl/7l3Q6czhqyWV85puz3dAvzmSaERyS+fm9BjFDcLWlyPHONxTQvsShfod1SYzBWTwBwKytS+AsiybkoEXU0SxSDck3ZZMPN2aMJJ+r";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhd6IKBQfbU30njoyJGx0JLzrWGahryW04ujJGJVjNb99AyPoPSE6ZSLS8o/dgaKIil2r0MhJQ9+oLyd8XY34d+5QmYCg1dKgNksL8//k8i8xd7NM53Pc9ZjJRtxYd+Z8LK9GVu0x5YVkFupPgdU/oCZ/XsedqXpYgEdOcrgLBmWpxoIeXEp1RK806NL6HTLKokev1lAdmjdJ7whjLIKO1Io2XNXjOF8C+fpTFjkpJuYf3MzuvDtj7vzEfu3gPZeLU7DcLH7DYa56V2xq7NgwxAu2K1N6RxeSmaIyk7PegVOfaNn7cnsRHRuSJmMaIglqNzS2YWFMJA3DeCNMxWq6OQIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://wdkecq24kk.52http.net/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url = "http://member.kedamall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
