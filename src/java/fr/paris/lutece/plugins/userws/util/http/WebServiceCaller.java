/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.userws.util.http;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;
import fr.paris.lutece.util.signrequest.HeaderHashAuthenticator;
import fr.paris.lutece.util.signrequest.NoSecurityAuthenticator;
import fr.paris.lutece.util.signrequest.RequestAuthenticator;
import fr.paris.lutece.util.signrequest.RequestHashAuthenticator;

import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;


/**
 *
 * WebServiceCaller
 *
 */
public class WebServiceCaller implements IWebServiceCaller
{
    /**
     * Private constructor
     */
    protected WebServiceCaller(  )
    {
    }

    /**
    * This method calls Rest WS to do an action
    * @param strUrl the url
    * @param authenticator the request authenticator
    * @param listElements the list of elements to include in the signature
    * @return the response as a string
    * @throws HttpAccessException the exception if there is a problem
    */
    public String callWebService( String strUrl, RequestAuthenticator authenticator, List<String> listElements )
        throws HttpAccessException
    {
        String strResponse = StringUtils.EMPTY;

        if ( AppLogService.isDebugEnabled(  ) )
        {
            AppLogService.debug( trace( strUrl, authenticator, listElements ) );
        }

        try
        {
            HttpAccess httpAccess = new HttpAccess(  );
            strResponse = httpAccess.doGet( strUrl, authenticator, listElements );
        }
        catch ( HttpAccessException e )
        {
            String strError = "Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage(  ), e );
            throw new HttpAccessException( strError, e );
        }

        return strResponse;
    }

    /**
     * Trace the web service caller
     * @param strUrl the url
     * @param authenticator the request authenticator
     * @param listElements the list of elements to include in the signature
     * @return the trace
     */
    public String trace( String strUrl, RequestAuthenticator authenticator, List<String> listElements )
    {
        StringBuilder sbTrace = new StringBuilder(  );
        sbTrace.append( "\n ---------------------- User Attribute WebService Call -------------------" );
        sbTrace.append( "\nWebService URL : " ).append( strUrl );

        sbTrace.append( "\nSecurity : " );
        sbTrace.append( "\nValues used for the request signature : " );

        for ( String strValue : listElements )
        {
            sbTrace.append( "\n   " ).append( strValue );
        }

        if ( authenticator instanceof RequestHashAuthenticator )
        {
            RequestHashAuthenticator auth = (RequestHashAuthenticator) authenticator;
            String strTimestamp = Long.toString( new Date(  ).getTime(  ) );
            String strSignature = auth.buildSignature( listElements, strTimestamp );
            sbTrace.append( "\n Request Authenticator : RequestHashAuthenticator" );
            sbTrace.append( "\n Timestamp sample : " ).append( strTimestamp );
            sbTrace.append( "\n Signature for this timestamp : " ).append( strSignature );
        }
        else if ( authenticator instanceof HeaderHashAuthenticator )
        {
            HeaderHashAuthenticator auth = (HeaderHashAuthenticator) authenticator;
            String strTimestamp = Long.toString( new Date(  ).getTime(  ) );
            String strSignature = auth.buildSignature( listElements, strTimestamp );
            sbTrace.append( "\n Request Authenticator : HeaderHashAuthenticator" );
            sbTrace.append( "\n Timestamp sample : " ).append( strTimestamp );
            sbTrace.append( "\n Signature for this timestamp : " ).append( strSignature );
        }
        else if ( authenticator instanceof NoSecurityAuthenticator )
        {
            sbTrace.append( "\n No request authentification" );
        }
        else
        {
            sbTrace.append( "\n Unknown Request authenticator" );
        }

        sbTrace.append( "\n --------------------------------------------------------------------" );

        return sbTrace.toString(  );
    }
}
