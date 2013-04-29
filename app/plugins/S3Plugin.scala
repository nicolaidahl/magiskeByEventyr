package plugins

import play.Plugin
import play.Configuration
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

object S3Plugin extends Plugin {
  val isEnabled = play.Configuration.root().getBoolean("aws.enabled")
  var amazonS3 : AmazonS3Client = null
  var s3Bucket : String = null
}

class S3Plugin (application: play.Application) extends Plugin {
	val AWS_S3_BUCKET = "aws.s3.bucket"
    val AWS_ACCESS_KEY = "aws.access.key"
    val AWS_SECRET_KEY = "aws.secret.key"

    override def onStart = {
	    val accessKey = application.configuration().getString(AWS_ACCESS_KEY)
        val secretKey = application.configuration().getString(AWS_SECRET_KEY)
        S3Plugin.s3Bucket = application.configuration().getString(AWS_S3_BUCKET)
        
        println("acceskey: " + accessKey);
	    println("secretkey: " + secretKey);
	    println("bucket: " + S3Plugin.s3Bucket);
        
        if ((accessKey != null) && (secretKey != null)) {
            val awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            S3Plugin.amazonS3 = new AmazonS3Client(awsCredentials);
        }
    }
	//TODO: Remove?
    override def enabled : Boolean = {
      val keys = application.configuration().keys()
      (keys.contains(AWS_ACCESS_KEY) && keys.contains(AWS_SECRET_KEY) && keys.contains(AWS_S3_BUCKET))
    }
}