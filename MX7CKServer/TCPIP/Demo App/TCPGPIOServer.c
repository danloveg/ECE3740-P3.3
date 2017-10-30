#define _SUPPRESS_PLIB_WARNING

#include "TCPIPConfig.h"

#if defined(STACK_USE_TCP_GPIO_SERVER)

#include "TCPIP Stack/TCPIP.h"
#include <ctype.h>
#include <string.h>
#include <assert.h>
#include "PortConfig.h"
#include "TCPGPIOServer.h"

// State variables
state myState = SM_OPEN_SERVER_SOCKET;
command myCommand = DO_NO_COMMAND;
parsedCommand parsedCmd = INVALID;

/*
 * Unnecessary for this application
 *
int menuState = 0;

const char *menu[] = {"\n\n    Enter a command to interact with board, or enter q to quit\n\n",
                      "LED1 on: LED1      LED1 off: ~LED1      LED2 on: LED2      LED2 off: ~LED2\n",
                      "LED3 on: LED3      LED3 off: ~LED3      LED4 on: LED4      LED4 off: ~LED4\n",
                      "Get BTN1 state: BTN1       Get BTN2 state: BTN2       Get BTN3 state: BTN3\n"};
*/

/*****************************************************************************
  Function:
        void TCPGPIOServer(void)

  Summary:
        Implements a simple TCP Server, which accepts a set of commands from the
        user to turn LEDs on the board on/off, and monitor push buttons.
  
  Description:
        Implements a simple TCP Server, which accepts a set of commands from the
        user to turn LEDs on the board on/off, and monitor push buttons.

  Precondition:
        TCP is initialized.

  Parameters:
        None

  Returns:
        None
 ***************************************************************************/
void TCPGPIOServer(void) {
    static TCP_SOCKET mySocket;
    WORD numBytes = 0;
    BYTE userCmd[MAX_CMD_LENGTH];

    switch (myState) {
        // Open a server socket
        case SM_OPEN_SERVER_SOCKET:
            mySocket = TCPOpen(0, TCP_OPEN_SERVER, TCP_GPIO_SERVER_PORT, TCP_PURPOSE_TCP_GPIO_SERVER);
            if (mySocket == INVALID_SOCKET) {
                return;
            }
            myState = SM_LISTEN_FOR_CLIENT_CONNECTION;
            break;


        // Listen for a client to connect
        case SM_LISTEN_FOR_CLIENT_CONNECTION:
            if (TCPIsConnected(mySocket) == TRUE) {
                myState = SM_DISPLAY_MENU;
            }
            break;


        // Display the menu
        case SM_DISPLAY_MENU:
            // Send connected message
            tcpSendMessageWithProtocol(mySocket, "Hello.");
            myState = SM_FIND_COMMAND;
            break;


        // Find the user's command
        case SM_FIND_COMMAND:
            // If the user has disconnected, somehow, close the connection
            if (TCPIsConnected(mySocket) == FALSE) {
                myState = SM_DISCONNECT_CLIENT;
                return;
            }

            // Get the number of bytes in the 'GET' queue
            numBytes = TCPIsGetReady(mySocket);

            // Otherwise, get the user's command
            if (numBytes > 0) {
                // Read from the socket
                tcpReadCommandWithProtocol(mySocket, userCmd, numBytes - 2);

                if (numBytes < 4 && (userCmd[0] == 'q' || userCmd[0] == 'd')) {
                    // User wants to disconnect or quit...
                    myState = SM_DISCONNECT_CLIENT;
                } else {
                    // User entered a command, find it to process
                    parsedCmd = findCommand((BYTE*)&userCmd);
                    myState = SM_PROCESS_COMMAND;
                }
            }
            break;


        // Act on the user's command
        case SM_PROCESS_COMMAND:
            {
                BOOL commandExecuted = executeCommand(mySocket, parsedCmd);

                if (commandExecuted == TRUE) {
                    myState = SM_FIND_COMMAND;
                }
            }
            break;


        // Disconnect the client   
        case SM_DISCONNECT_CLIENT:
            // Disconnect acknowledge
            tcpSendDisconnectAcknowledge(mySocket);
            // Discard socket
            TCPDiscard(mySocket);
            // Disconnect socket
            TCPDisconnect(mySocket);
            // And start listening for other connections
            myState = SM_LISTEN_FOR_CLIENT_CONNECTION;
            // menuState = 0;
            break;
    }
}

parsedCommand findCommand(BYTE* unparsedCommand) {
    char *usersCommand = (char*) unparsedCommand;
    parsedCommand cmd;
     
    if (strcmp(usersCommand, "L1on") == 0) { cmd = LED1; }
    else if (strcmp(usersCommand, "L2on") == 0) { cmd = LED2; }
    else if (strcmp(usersCommand, "L3on") == 0) { cmd = LED3; }
    else if (strcmp(usersCommand, "L4on") == 0) { cmd = LED4; }
    else if (strcmp(usersCommand, "L1off") == 0) { cmd = NOT_LED1; }
    else if (strcmp(usersCommand, "L2off") == 0) { cmd = NOT_LED2; }
    else if (strcmp(usersCommand, "L3off") == 0) { cmd = NOT_LED3; }
    else if (strcmp(usersCommand, "L4off") == 0) { cmd = NOT_LED4; }
    else if (strcmp(usersCommand, "gpb1") == 0) { cmd = BTN1; }
    else if (strcmp(usersCommand, "gpb2") == 0) { cmd = BTN2; }
    else if (strcmp(usersCommand, "gpb3") == 0) { cmd = BTN3; }
    else { cmd = INVALID; }
    
    return cmd;
}

BOOL executeCommand(TCP_SOCKET socket, parsedCommand cmd) {
    BOOL executed = FALSE;

    // If the socket is not ready to put, return
    if (TCPIsPutReady(socket) >= MAX_RESPONSE_LENGTH) {
        switch (cmd) {
            case LED1:
                ledOn(1, TRUE);
                tcpSendMessageWithProtocol(socket, "LED1 is ON");
                break;
            case NOT_LED1:
                ledOn(1, FALSE);
                tcpSendMessageWithProtocol(socket, "LED1 is OFF");
                break;
            case LED2:
                ledOn(2, TRUE);
                tcpSendMessageWithProtocol(socket, "LED2 is ON");
                break;
            case NOT_LED2:
                ledOn(2, FALSE);
                tcpSendMessageWithProtocol(socket, "LED2 is OFF");
                break;
            case LED3:
                ledOn(3, TRUE);
                tcpSendMessageWithProtocol(socket, "LED3 is ON");
                break;
            case NOT_LED3:
                ledOn(3, FALSE);
                tcpSendMessageWithProtocol(socket, "LED3 is OFF");
                break;
            case LED4:
                ledOn(4, TRUE);
                tcpSendMessageWithProtocol(socket, "LED4 is ON");
                break;
            case NOT_LED4:
                ledOn(4, FALSE);
                tcpSendMessageWithProtocol(socket, "LED4 is OFF");
                break;
            case BTN1:
                if (buttonPressed(1) == TRUE) {
                    tcpSendMessageWithProtocol(socket, "BTN1 is pressed");
                } else {
                    tcpSendMessageWithProtocol(socket, "BTN1 is NOT pressed");
                }
                break;
            case BTN2:
                if (buttonPressed(2) == TRUE) {
                    tcpSendMessageWithProtocol(socket, "BTN1 is pressed");
                } else {
                    tcpSendMessageWithProtocol(socket, "BTN2 is NOT pressed");
                }
                break;
            case BTN3:
                if (buttonPressed(3) == TRUE) {
                    tcpSendMessageWithProtocol(socket, "BTN3 is pressed");
                } else {
                    tcpSendMessageWithProtocol(socket, "BTN3 is NOT pressed");
                }
                break;
            default:
                tcpSendMessageWithProtocol(socket, "Invalid Command");
                break;
        }

        executed = TRUE;
    }

    return executed;
}

void tcpSendMessageWithProtocol(TCP_SOCKET socket, char* message) {
    TCPPutArray(socket, (BYTE*) message, strlen(message));
    TCPFlush(socket);
    TCPPut(socket, (BYTE) TERMINATING_BYTE);
}

void tcpSendDisconnectAcknowledge(TCP_SOCKET socket) {
    tcpSendMessageWithProtocol(socket, "Disconnect acknowledged.");
}

void tcpReadCommandWithProtocol(TCP_SOCKET socket, BYTE* command, unsigned int numBytes) {
    TCPGetArray(socket, command, numBytes);
    TCPDiscard(socket);

    // Replace terminating byte with null terminator
    command[numBytes - 1] = '\0';
}

#endif //#if defined(STACK_USE_TCP_TO_UPPER_SERVER)
