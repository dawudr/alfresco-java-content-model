<%--
* Copyright (C) 2005-2007 Alfresco Software Limited.

* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

* As a special exception to the terms and conditions of version 2.0 of
* the GPL, you may redistribute this Program in connection with Free/Libre
* and Open Source Software ("FLOSS") applications as described in Alfresco's
* FLOSS exception.  You should have recieved a copy of the text describing
* the FLOSS exception, and it is also available here:
* http://www.alfresco.com/legal/licensing"
--%>
<%-- Title bar area --%>
<table cellspacing=0 cellpadding=2 width=100%>
	<tr class="atb-header">
		<td colspan="2">
			<div>
				<table cellpadding="0" width="100%" cellspacing="0" align="center">
					<tr>
						<td valign="middle">
							<p>
								<img
									src="<%=request.getContextPath()%>/images/ai/AI_global_candle.gif"
									align="absmiddle" border="0" width="15" height="19" />
								Amnesty International &gt;
								<a href='https://intranet.amnesty.org'>Intranet</a> &gt;
								<a href='<%=request.getContextPath()%>'>Aidoc</a>
							</p>
						</td>
						<td valign="middle" align="right">
							&nbsp;
						</td>
					</tr>
				</table>
			</div>
		</td>
		<td>
			<table cellspacing=2 cellpadding=0 width=100%>
				<tr>
					<td>
						<%-- admin user only actions --%>
						<a:booleanEvaluator
							value="#{NavigationBean.currentUser.admin == true}" id="evalA">
							<a:actionLink value="#{msg.admin_console}"
								image="/images/icons/admin_console.gif" showLink="false"
								action="dialog:adminConsole" id="link11_1" />
						</a:booleanEvaluator>
					</td>
					<td width=12>
						&nbsp;
					</td>
					<td>
                  <%-- user preferences --%>
                  <a:actionLink value="#{msg.user_console}" 
                                image="/images/icons/user_console.gif" 
                                showLink="false" 
                                action="dialog:userConsole" 
                                actionListener="#{UsersDialog.setupUserAction}" 
                                id="alf_user_console">
                     <f:param name="id" value="#{NavigationBean.currentUser.person.id}" />
                  </a:actionLink>
					</td>
					<td width=8>
						&nbsp;
					</td>
					<td>
						<a:actionLink value="#{msg.toggle_shelf}"
							image="/images/icons/shelf.gif" showLink="false"
							actionListener="#{NavigationBean.toggleShelf}" />
					</td>
					<td width=8>
						&nbsp;
					</td>
					<td>
						<a:actionLink value="#{msg.help}"
							image="/images/icons/Help_icon.gif" showLink="false"
							href="#{NavigationBean.helpUrl}" target="help" />
					</td>
					<td width=8>
						&nbsp;
					</td>
					<td>
						<nobr>
							<a:actionLink id="logout" image="/images/icons/logout.gif"
								value="#{msg.logout} (#{NavigationBean.currentUser.userName})"
								rendered="#{NavigationBean.isGuest == false}"
								action="#{LoginBean.logout}" immediate="true" />
							<a:actionLink id="login" image="/images/icons/login.gif"
								value="#{msg.login} (#{NavigationBean.currentUser.userName})"
								rendered="#{NavigationBean.isGuest == true}"
								action="#{LoginBean.logout}" />
						</nobr>
					</td>
				</tr>
			</table>
		</td>
	</tr>

	<tr>
		<%-- Top level toolbar and company logo area --%>
		<td width=100%>
			<table cellspacing=0 cellpadding=0 width=100%>
				<tr>
					<td style="padding-right:4px;">
						<a:actionLink image="/images/ai/aibanner.gif" value="Alfresco"
							tooltip="Alfresco" showLink="false" action="about" />
					</td>
					<td>
						&nbsp;
					</td>
				</tr>
			</table>
		</td>

		<%-- Help area --%>
		<td>

		</td>

		<%-- Search area --%>
		<td>
			<table cellspacing=0 cellpadding=0 width=100%>
				<tr>
					<td>
						<img
							src="<%=request.getContextPath()%>/images/parts/searchbar_begin.gif"
							width=6 height=31>
					</td>
					<td width=100%
						style="background-image: url(<%=request.getContextPath()%>/images/parts/searchbar_bg.gif)">
						<r:simpleSearch id="search" actionListener="#{BrowseBean.search}" />
					</td>
					<td>
						<img
							src="<%=request.getContextPath()%>/images/parts/searchbar_end.gif"
							width=6 height=31>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td colspan="3">
			<hr size="1" />
		</td>
	</tr>
</table>
