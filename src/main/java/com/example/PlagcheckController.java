package com.example;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import copyleaks.sdk.api.*;
import copyleaks.sdk.api.exceptions.CommandFailedException;
import copyleaks.sdk.api.exceptions.SecurityTokenException;
import copyleaks.sdk.api.models.ProcessOptions;
import copyleaks.sdk.api.models.ResultRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;


@Controller
public class PlagcheckController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //store credentials for copyleaks api access
    // best practice would be to use encrypted credent
    private static final String clEmail = "spspkand@uwaterloo.ca";
    private static final String clKey = "C31E1C54-E43A-4F27-997A-D135B49E8C96";

    // map a get request to the plagtextForm method all of the requestmaping
    // methods return strings that should correspond to thymeleaf views
    // registered in the resources/templates folder. Attributes referred to in
    // the templates need to be populated in the controller as well.
    @GetMapping("/")
    public String plagtextForm(Model model) {
        model.addAttribute("plagtext", new Plagtext());
        return "plagtext";
    }

    //post requests to the "submit" are handled by plagtextSubmit Take the
    // content from the form and submit to it to create-by-text endpoint of the
    // copyleaks.
    @PostMapping("/submit")
    public String plagtextSubmit(@ModelAttribute Plagtext plagtext, Model model) {
        CopyleaksCloud copyleaks = new CopyleaksCloud(eProduct.Academic);
        ResultRecord[] results;
        CopyleaksProcess createdProcess;
        // process options to control headers used by the copyleaks api
        ProcessOptions scanOptions = new ProcessOptions();
        scanOptions.setSandboxMode(false);
        try
            {
                log.info("Logging into copyleaks, with {} and {}", this.clEmail, this.clKey);
                copyleaks.Login(this.clEmail, this.clKey);
                int creditBalance = copyleaks.getCredits();
                log.info("You have {} credits available.", creditBalance);
                // scanOptions.setInProgressResultsHttpCallback(new
                   // URI("https://safe-fjord-76241.herokuapp.com?pid=%7BPID%7D"));
                createdProcess = copyleaks.CreateByText(plagtext.getContent(), scanOptions);
                log.info("a process has been created with copyleaks, {}", createdProcess.toString());

                int percents = 0;
                while (percents != 100 && (percents = createdProcess.getCurrentProgress()) <= 100)
                    {
                        log.info("Text analysis has progressed to {} %", percents);

                        if(percents != 100)
                            Thread.sleep(5000);

                    }
                results = createdProcess.GetResults();
                String summ = String.format("There were %1$s copied words (%2$s%%)",
                                           results[0].getNumberOfCopiedWords(), results[0].getPercents());
                Plagresult resobj = new Plagresult();
                resobj.setSummary(summ);
                resobj.setReportURL(results[0].getEmbededComparison());
                resobj.setTitle(results[0].getTitle());
                // resobj.setSummary("A sample summary with 10 failed words and 10% plagiarized");
                // resobj.setReportURL("https://copyleaks.com/compare-embed/fb82aeab-7670-4088-a32d-6a0a96125eb4/2155490");
                // resobj.setTitle("Your Final report");
                model.addAttribute("plagresult", resobj);

            }
        catch (CommandFailedException copyleaksException)
            {
                log.error("Error Code {}", copyleaksException.getCopyleaksErrorCode());
                log.error(copyleaksException.toString());
            }
        catch (SecurityTokenException sexp)
            {
                log.error("The login token needs to be renewed as it's either invalid or has expired");
                log.error(sexp.toString());
            }
        catch (Exception ex)
            {
                log.error("There was a generic exception which was caught");
                log.error("{}", ex);
            }

        return "result";
    }
}
