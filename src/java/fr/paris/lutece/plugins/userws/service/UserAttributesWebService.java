/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.userws.service;

import fr.paris.lutece.plugins.userws.service.signrequest.UserAttributesRequestAuthenticatorService;
import fr.paris.lutece.plugins.userws.util.http.IWebServiceCaller;
import fr.paris.lutece.portal.service.security.UserAttributesService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * UserAttributesWebService
 *
 */
public class UserAttributesWebService implements UserAttributesService
{
    // CONSTANTS
    private static final String JSON_EXTENSION = ".json";
    private static final String SLASH = "/";
    private static final String QUESTION_MARK = "?";
    private static final String EQUAL = "=";

    // TAGS
    private static final String TAG_USER_ATTRIBUTES = "user-attributes";
    private static final String TAG_USER_ATTRIBUTE_KEY = "user-attribute-key";
    private static final String TAG_USER_ATTRIBUTE_VALUE = "user-attribute-value";

    // PROPERTIES
    private static final String PROPERTY_REST_USER_ATTRIBUTE_WEBAPP_URL = "userws.rest.userAttribute.webapp.url";

    // PARAMETERS
    private static final String PARAMETER_USER_GUID = "user_guid";

    // VARIABLES
    private IWebServiceCaller _webServiceCaller;

    /**
     * Private constructor
     */
    public UserAttributesWebService(  )
    {
    }

    /**
     * Set the web service caller
     * @param webServiceCaller the web service caller
     */
    public void setWebServiceCaller( IWebServiceCaller webServiceCaller )
    {
        _webServiceCaller = webServiceCaller;
    }

    /**
     * Calls the rest ws to fetch the user attributes from the given user guid
     * @param strUserGuid the user guid
     * @return a map containing the user attributes
     */
    public Map<String, String> getAttributes( String strUserGuid )
    {
        Map<String, String> mapUserAttributes = new HashMap<String, String>(  );

        StringBuilder sbRestUrl = new StringBuilder(  );
        sbRestUrl.append( AppPropertiesService.getProperty( PROPERTY_REST_USER_ATTRIBUTE_WEBAPP_URL ) );
        sbRestUrl.append( strUserGuid );
        sbRestUrl.append( JSON_EXTENSION );
        sbRestUrl.append( QUESTION_MARK + PARAMETER_USER_GUID + EQUAL + strUserGuid );

        List<String> listElements = new ArrayList<String>(  );
        listElements.add( strUserGuid );

        String strJson = StringUtils.EMPTY;

        try
        {
            strJson = _webServiceCaller.callWebService( sbRestUrl.toString(  ),
                    UserAttributesRequestAuthenticatorService.getRequestAuthenticator(  ), listElements );

            JSONObject jsonAttributes = (JSONObject) JSONSerializer.toJSON( strJson );
            JSONArray jsonArray = jsonAttributes.getJSONArray( TAG_USER_ATTRIBUTES );

            for ( int i = 0; i < jsonArray.size(  ); i++ )
            {
                JSONObject jsonAttribute = jsonArray.getJSONObject( i );
                String strKey = jsonAttribute.getString( TAG_USER_ATTRIBUTE_KEY );
                String strValue = jsonAttribute.getString( TAG_USER_ATTRIBUTE_VALUE );
                mapUserAttributes.put( strKey, strValue );
            }
        }
        catch ( HttpAccessException e )
        {
            throw new AppException( e.getMessage(  ), e );
        }

        return mapUserAttributes;
    }

    /**
     * Calls the rest ws to fetch the user attribute
     * @param strUserGuid the user guid
     * @param strAttributeKey the attribute key
     * @return the user attribute value
     */
    public String getAttribute( String strUserGuid, String strAttributeKey )
    {
        String strUserAttribute = StringUtils.EMPTY;

        StringBuilder sbRestUrl = new StringBuilder(  );
        sbRestUrl.append( AppPropertiesService.getProperty( PROPERTY_REST_USER_ATTRIBUTE_WEBAPP_URL ) );
        sbRestUrl.append( strUserGuid );
        sbRestUrl.append( SLASH );
        sbRestUrl.append( strAttributeKey );
        sbRestUrl.append( QUESTION_MARK + PARAMETER_USER_GUID + EQUAL + strUserGuid );

        List<String> listElements = new ArrayList<String>(  );
        listElements.add( strUserGuid );

        try
        {
            strUserAttribute = _webServiceCaller.callWebService( sbRestUrl.toString(  ),
                    UserAttributesRequestAuthenticatorService.getRequestAuthenticator(  ), listElements );
        }
        catch ( HttpAccessException e )
        {
            throw new AppException( e.getMessage(  ), e );
        }

        return strUserAttribute;
    }
}
