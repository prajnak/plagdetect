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
    private static final String clKey = "8F439644-95FE-4BC3-8069-CC437992B4BC";

    // map a get request to the plagtextForm method
    @GetMapping("/")
    public String plagtextForm(Model model) {
        model.addAttribute("plagtext", new Plagtext());
        return "plagtext";
    }

    //post requests to the "submit" are handled by plagtextSubmit Take the
    // content from the form and submit to it to create-by-text endpoint of the
    // copyleaks.
    @PostMapping("/submit")
    public String plagtextSubmit(@ModelAttribute Plagtext plagtext) {
        CopyleaksCloud copyleaks = new CopyleaksCloud(eProduct.Academic);
        // process options to control headers used by the copyleaks api
        ProcessOptions scanOptions = new ProcessOptions();
        scanOptions.setSandboxMode(true);
        try
            {
                log.info("Logging into copyleaks, with {} and {}", this.clEmail, this.clKey);
                copyleaks.Login(this.clEmail, this.clKey);
                int creditBalance = copyleaks.getCredits();
                log.info("You have {} credits available.", creditBalance);
                scanOptions.setInProgressResultsHttpCallback(new
                                                             URI("http://requestb.in/ooi6h5oo?inspectpid=%7BPID%7D"));
                ResultRecord[] results;
                CopyleaksProcess createdProcess = copyleaks.CreateByText(plagtext.getContent(), scanOptions);
                log.info("a process has been created with copyleaks, {}", createdProcess.toString());


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

    // @PostMapping("/callback?{pid}")
    // public String handleCallback(@PathVariable String procid) {
    //     log.info("Received a callback from ");
    // }


}
