<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:forEach items="${tree_si_list}" var="clazz">
  <div class="my-3 px-3">

    <div class="row">
      <b class="custom-link text-primary">${clazz.label}</b>
    </div>

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
              <c:choose>
                <c:when test="${fn:contains(entry.key,'definition')
                                || fn:contains(entry.key,'comment')
                                || fn:contains(entry.key,'description')
                                || fn:contains(entry.key,'SubClassOf')
                        }"> 
                  <span class="mb-3">${details}</span>
                </c:when>
                <c:otherwise>
                  <a
                    href="${pageContext.request.contextPath}/search?query=${details}"
                    class="custom-link mb-3">${details}</a>

                </c:otherwise>
              </c:choose>

            </c:forEach>

          </c:when>
          <c:otherwise>
            <br />
            <ul>
              <c:forEach var="details" items="${entry.value}">

                <c:choose>
                  <c:when test="${fn:contains(entry.key,'definition')
                                  || fn:contains(entry.key,'comment')
                                  || fn:contains(entry.key,'description')
                                  || fn:contains(entry.key,'SubClassOf')
                                  || fn:contains(entry.key,'module')
                                  || fn:contains(entry.key,'label')
                                  || fn:contains(entry.key,'editorialNote')
                                  || fn:contains(entry.key,'explanatoryNote')
                          }"> 
                    <li>${details}</li>
                  </c:when>
                  <c:otherwise>
                    <li><a
                        href="${pageContext.request.contextPath}/search?query=${details}"
                        class="custom-link">${details}</a></li>

                  </c:otherwise>
                </c:choose>


              </c:forEach>
            </ul>

          </c:otherwise>
        </c:choose>
      </div>
    </c:forEach>

    <c:if test="${empty clazz.properties}">
      <ul>

      </ul>
    </c:if>
    <div class="border-bottom col-12 mt-1"></div>
  </div>
</c:forEach>
