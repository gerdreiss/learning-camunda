import java.util.logging.Logger
import org.camunda.bpm.client.ExternalTaskClient
import java.awt.Desktop
import java.net.URI

@main def chargeCardWorker: Unit =
  val logger = Logger.getLogger(getClass.getName)

  val client = ExternalTaskClient
    .create()
    .baseUrl("http://localhost:8080/engine-rest")
    .asyncResponseTimeout(10000)
    .build()

  // subscribe to an external task topic as specified in the process definition
  client
    .subscribe("charge-card")
    .lockDuration(10000)
    .handler { (externalTask, externalTaskService) =>
      logger.info(s"Received external task ${externalTask.getId}")

      // get the process variables
      val item   = externalTask.getVariable("item")
      val amount = externalTask.getVariable("amount")

      logger.info("Charging credit card with an amount of '" + amount + "'€ for the item '" + item + "'...")

      Desktop
        .getDesktop()
        .browse(new URI("https://docs.camunda.org/get-started/quick-start/complete"));

      // Complete the task
      externalTaskService.complete(externalTask);
    }
    .open()
