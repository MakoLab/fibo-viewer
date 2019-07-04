<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:forEach items="${details_list}" var="clazz">
  <div class="my-3 px-3">

    <div class="row">
      <b class="custom-link text-primary">${clazz.label}</b>
    </div>
    <c:set var = "context" value = "http://localhost:8080"/>
    <c:forEach var="entry" items="${clazz.properties}">
      <div class="row">
        <c:choose>
          <c:when test="${fn:length(entry.value)==1}">
            <b class="mr-1">${entry.key}: </b>
          </c:when>
          <c:otherwise>
            <b class="col-12 px-0">${entry.key}: </b>
          </c:otherwise>
        </c:choose>
        <c:choose>
          <c:when test="${fn:length(entry.value)==1}">
            <c:forEach var="details" items="${entry.value}">
              <span class="mb-3"><weasel:Render property="${details}" searchPath="${context}"/></span>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <br />
            <ul>
              <c:forEach var="details" items="${entry.value}">
                <weasel:Render property="${details}" elementWrapper="li" searchPath="${context}"/>
              </c:forEach>
            </ul>

          </c:otherwise>
        </c:choose>
      </div>
    </c:forEach>

    <c:if test="${empty clazz.properties}">
      There is nothing to display...
    </c:if>
    <div class="border-bottom col-12 mt-1"></div>
  </div>
</c:forEach>
