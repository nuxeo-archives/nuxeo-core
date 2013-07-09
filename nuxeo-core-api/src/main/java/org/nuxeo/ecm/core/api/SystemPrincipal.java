/*
 * (C) Copyright 2006-2012 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Thomas Roger <troger@nuxeo.com>
 */

package org.nuxeo.ecm.core.api;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.runtime.api.login.LoginComponent;
import org.nuxeo.runtime.api.login.LoginComponent.SystemID;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class SystemPrincipal extends SystemID implements NuxeoPrincipal {

    private static final long serialVersionUID = -3381784063138281706L;

    private static final char[] SYS_PASSWORD = null;

    private static final List<String> SYS_GROUPS = Collections.unmodifiableList(Arrays.asList(SecurityConstants.ADMINISTRATORS));

    private static final List<String> SYS_ROLES = Collections.unmodifiableList(new ArrayList<String>());

    private String origUserName;

    private int hash;

    public SystemPrincipal(String origUserName) {
        this.origUserName = origUserName == null ? LoginComponent.SYSTEM_USERNAME
                : origUserName;
        computeHash();
    }

    private void computeHash() {
        if (origUserName != null) {
            hash = (LoginComponent.SYSTEM_USERNAME + "-" + origUserName).hashCode();
        } else {
            hash = LoginComponent.SYSTEM_USERNAME.hashCode();
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SystemPrincipal) {
            if (!LoginComponent.SYSTEM_USERNAME.equals(((Principal) other).getName())) {
                return false;
            }
            if (origUserName == null) {
                return ((SystemPrincipal) other).origUserName == null;
            } else {
                return origUserName.equals(((SystemPrincipal) other).origUserName);
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String getCompany() {
        return "Nuxeo";
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public void setEmail(String email) {

    }

    @Override
    public String getFirstName() {
        return "System";
    }

    @Override
    public String getLastName() {
        return "System";
    }

    @Override
    public String getName() {
        return LoginComponent.SYSTEM_USERNAME;
    }

    @Override
    public List<String> getGroups() {
        return SYS_GROUPS;
    }

    @Override
    public List<String> getAllGroups() {
        return SYS_GROUPS;
    }

    @Override
    public List<String> getRoles() {
        return SYS_ROLES;
    }

    @Override
    public String getPassword() {
        if (SYS_PASSWORD == null) {
            return null;
        }
        return new String(SYS_PASSWORD);
    }

    @Override
    public String getPrincipalId() {
        return "";
    }

    @Override
    public String getOriginatingUser() {
        return origUserName;
    }

    @Override
    public void setOriginatingUser(String originatingUser) {
        origUserName = originatingUser;
        computeHash();
    }

    @Override
    public DocumentModel getModel() {
        return null;
    }

    @Override
    public void setCompany(String company) {
    }

    @Override
    public void setFirstName(String firstName) {
    }

    @Override
    public void setLastName(String lastName) {
    }

    @Override
    public void setName(String userName) {
    }

    @Override
    public void setGroups(List<String> groups) {
    }

    @Override
    public void setRoles(List<String> roles) {
    }

    @Override
    public void setPassword(String password) {
    }

    @Override
    public void setPrincipalId(String principalId) {
    }

    @Override
    public void setModel(DocumentModel model) {
    }

    @Override
    public boolean isMemberOf(String group) {
        return SYS_GROUPS.contains(group);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean isAdministrator() {
        return true;
    }

    @Override
    public String getTenantId() {
        return null;
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }

}

