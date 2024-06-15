<?php

    if(isset($_POST['submit'])){
        $to = "jlaurent@et.esiea.fr"; 
        $from = $_POST['email']; 
        $subject = $_POST['subject'];
        $message = $from . " wrote the following:" . "\n\n" . $_POST['message'];

        $headers = "From:" . $from;
        if(mail($to,$subject,$message,$headers)){
            //echo "Mail Sent. Thank you " . $from . ", we will contact you shortly.";
        } else{
            //echo "Mail not sent. Please try again.";
        }
    }
?>
<script>
    location.href = "https://soundwaveesiea.000webhostapp.com/";
</script>

