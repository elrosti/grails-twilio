package com.novadge.twilio

import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair

class CallService {

    static transactional = false

    def grailsApplication

    /**
     * Send message with Twilio REST API.
     * @param props: A map of parameters:
     * <ul>
     * <li>to: recipient number</li>
     * <li>from: sender number ( from Twilio )</li>
     * <li>url: Url with call information when the calls connect</li>
     * <li>method: HTTP method to access the mediaUrl</li>
     * <li>statusCallback: URL where twilio will send information when a call is completed</li>
     * <li>statusCallbackMethod: HTTP method to access the statusCallback URL</li>
     * </ul>
     * @returns the response
     */
    CloseableHttpResponse call(Map props) {
        call(props?.to, props?.from, props?.url, props?.method, props?.statusCallback, props?.statusCallbackMethod)
    }

    /**
     * Send message with Twilio REST API.
     * @param to: recipient number
     * @param from: sender number ( from twilio )
     * @param callInformationUrl: Url with call information when the calls connect
     * @param method: HTTP method to access the mediaUrl
     * @param statusCallback: URL where twilio will send information when a call is completed
     * @param statusCallbackMethod: HTTP method to access the statusCallback URL
     * @returns the response
     */
    CloseableHttpResponse call(String to, String from, String callInformationUrl, String method, String statusCallback, String statusCallbackMethod) {
        def conf = grailsApplication.config.twilio
        String twilioHost = conf.host
        String apiID = conf.apiID
        String apiPass = conf.apiPass
        String url = conf.callUrl

        call(twilioHost, apiID, apiPass, url, to, from, callInformationUrl, method, statusCallback, statusCallbackMethod)
    }

    /**
     * Send message with Twilio REST API.
     * @param twilioHost: host address for twilio
     * @param apiID : Twilio API ID
     * @param apiPass : Twilio API password
     * @param url : Twilio API endpoint for calls
     * @param to: Receiver number
     * @param from: Caller number ( from twilio )
     * @param body: message body
     * @param callInformationUrl: Url with call information when the calls connect
     * @param method: HTTP method to access the mediaUrl
     * @param statusCallback: URL where twilio will send information when a call is completed
     * @param statusCallbackMethod: HTTP method to access the statusCallback URL
     * @returns :CloseableHttpResponse
     */
    CloseableHttpResponse call(String twilioHost, String apiID, String apiPass, String url, String to, String from, String callInformationUrl, String method, String statusCallback = "", String statusCallbackMethod = "") {

        CredentialsProvider provider = new BasicCredentialsProvider()
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(apiID, apiPass)
        provider.setCredentials(AuthScope.ANY, credentials)
        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build()

        HttpPost httpPost = new HttpPost(twilioHost + url)
        List<BasicNameValuePair> params = [
            new BasicNameValuePair("To", to), // Recipients phone number.
            new BasicNameValuePair("From", from), // Your phone number ( Twilio ).
            new BasicNameValuePair("Url", callInformationUrl)]

        if(method){
            params << new BasicNameValuePair("Method", method)
        }

        if (statusCallback) {
            params << new BasicNameValuePair("StatusCallback", statusCallback)
        }

        if (statusCallbackMethod) {
            params << new BasicNameValuePair("StatusCallbackMethod", statusCallbackMethod)
        }

        httpPost.entity = new UrlEncodedFormEntity(params)
        return client.execute(httpPost)
    }
}