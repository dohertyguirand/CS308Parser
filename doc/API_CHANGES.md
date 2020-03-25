One of the main implementations that we did not specifically describe in the original design plan was that of the controller.
In our current implementation of the code, we have a Controller class handling the communication between front and back end 
elements. This class has a main public update method that handles the reflection of changes in the back end on the 
actual visuals seen by the user. This method reflects what was intended by the front end internal API we planned, though 
we found that the other methods were not necessary within that specific class. Within this method, public methods from the models and visuals are utilized to change properties
within each class. Besides this, the Controller also has public methods to send aspects of the models to other portions of the 
program that need information from the model such as color changing buttons in the user interface. 

All of the commands implement an interface which allows them to use a getNumParams method which is used in the parser when
running through the stacks that contain user input command info. Additionally, each model in the back end ended up having more
public methods than intended, mostly for providing more information that needed to be provided to the user in the UI such as 
variable names and values as well as method bodies. Specific UI portions also have unique public methods to update their contents
such as a public update history method to keep track of old user inputs or sending and receiving error methods.

