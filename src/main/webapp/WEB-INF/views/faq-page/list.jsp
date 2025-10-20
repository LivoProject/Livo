<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>


<section id="notice" class="container">
    <h3>자주 묻는 질문 (FAQ)</h3>


    <div class="accordion" id="faqAccordion">
        <c:forEach var="faq" items="${faqs}" varStatus="i">
          <div class="accordion-item mb-3">
            <h2 class="accordion-header" id="heading${i.index}">
              <button class="accordion-button ${i.first ? "" : "collapsed"}"
                      type="button"
                      data-bs-toggle="collapse"
                      data-bs-target="#collapse${i.index}"
                      aria-expanded="${i.first}"
                      aria-controls="collapse${i.index}">
                ${faq.question}
              </button>
            </h2>
            <div id="collapse${i.index}"
                 class="accordion-collapse collapse ${i.first ? "show" : ""}"
                 aria-labelledby="heading${i.index}"
                 data-bs-parent="#faqAccordion">
              <div class="accordion-body">
                ${faq.answer}
              </div>
            </div>
          </div>
        </c:forEach>

    </div>

</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
