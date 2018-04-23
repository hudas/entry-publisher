package org.ignas.datapublisher;

import org.ignas.datapublisher.dto.EvaluationDTO;
import org.ignas.datapublisher.dto.LocationDTO;
import org.ignas.datapublisher.dto.MarkDTO;
import org.ignas.datapublisher.repositories.TestCaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class Publisher {

    private static final Logger LOG = LoggerFactory.getLogger(DatapublisherApplication.class);

    @Autowired
    private TestCaseRepository testCaseRepository;

    public Publisher() {
    }

    @Transactional
    public Integer publishBatch(Integer batchSize) {
        LOG.debug("SEARCHING");

        List<TestCase> cases = testCaseRepository.findByProcessedIsFalseOrderByIdAsc(PageRequest.of(0, batchSize));

        LOG.debug("FOUND: {} cases", cases.size());

        RestTemplate tempalte = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LoggingRequestInterceptor());

        tempalte.setInterceptors(interceptors);

        Long lastProcessedId = 0l;

        for(TestCase it : cases) {
            EvaluationDTO evaluationReq = new EvaluationDTO(
                    it.getId() + "",
                    it.getDebtor(),
                    it.getDebtor(),
                    it.getCreditor(),
                    it.getAmount().floatValue(),
                    it.getTime().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.sxxx")),
                    new LocationDTO(
                            it.getLocationLatitude().setScale(6).toString(),
                            it.getLocationLongtitude().setScale(6).toString()
                    )
            );

            if (evaluationReq.getTransactionId().equals("4825")) {
                System.out.println("PAUSE");
            }

            try {
                tempalte.postForEntity(
                        "http://206.189.60.10:8081/evaluate-fraud", evaluationReq, String.class, new HashMap<>());

                if (it.isFraud()) {
                    MarkDTO markReq = new MarkDTO(it.getId() + "");

                    try {
                        Thread.sleep(100l);
                        tempalte.postForEntity(
                                "http://206.189.60.10:8081/mark-fraudulent", markReq, String.class, new HashMap<>());

                    } catch (ResourceAccessException ex) {
                        LOG.error(ex.getMessage());
                        break;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ResourceAccessException ex) {
                LOG.error(ex.getMessage());
                break;
            }

            lastProcessedId = it.getId();
        }


//        List<TestCase> fraudCases = cases.stream()
//                .filter(TestCase::isFraud)
//                .collect(Collectors.toList());
//
//        fraudCases.forEach(it -> {
//            MarkDTO markReq = new MarkDTO(it.getId() + "");
//
//            try {
//                tempalte.postForEntity(
//                        "http://localhost:8081/mark-fraudulent", markReq, String.class, new HashMap<>());
//
//            } catch (ResourceAccessException ex) {
//                LOG.error(ex.getMessage());
//                return;
//            }
//
//        });

//        cases.forEach(it -> it.markProcessed());
        int updated = testCaseRepository.updateProcessed(lastProcessedId);
        LOG.error("Updated : " + updated);

        return cases.size();
    }
}
