<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="menuTitle" required="true" type="java.lang.String"%>
<section title="<%=title%>" menuTitle="<%=menuTitle%>">
<jsp:doBody/>
</section>
