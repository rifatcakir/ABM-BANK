import {useEffect, useState} from "react";
import {AssistantService, LoanService} from "Frontend/generated/endpoints";
import LoanDetails from "../generated/com/abmbank/dto/LoanDetails";
import {GridColumn} from "@vaadin/react-components/GridColumn";
import {Grid} from "@vaadin/react-components/Grid";
import {MessageInput} from "@vaadin/react-components/MessageInput";
import {nanoid} from "nanoid";
import {SplitLayout} from "@vaadin/react-components/SplitLayout";
import Message, {MessageItem} from "../components/Message";
import MessageList from "Frontend/components/MessageList";

type LoanStatus = 'APPROVED' | 'PENDING' | 'REJECTED' | 'CANCELLED' | 'PAID';

export default function Index() {
  const [chatId, setChatId] = useState(nanoid());
  const [working, setWorking] = useState(false);
  const [loans, setLoans] = useState<LoanDetails[]>([]);
  const [messages, setMessages] = useState<MessageItem[]>([{
    role: 'assistant',
    content: 'Welcome to ABM BANK! How can I help you?'
  }]);

  useEffect(() => {
    // Update loans when we have received the full response
    if (!working) {
      LoanService.getLoans().then((response) => {
        if (response) {
          // Filter out any undefined values and ensure type safety
          const validLoans = response.filter((loan): loan is LoanDetails => loan !== undefined);
          setLoans(validLoans);
        }
      }).catch(error => {
        console.error('Error fetching loans:', error);
        setLoans([]); // Reset loans on error
      });
    }
  }, [working]);

  function addMessage(message: MessageItem) {
    setMessages(messages => [...messages, message]);
  }

  function appendToLatestMessage(chunk: string | undefined) {
    if (!chunk) return;
    
    setMessages(messages => {
      const latestMessage = messages[messages.length - 1];
      if (!latestMessage) return messages;
      
      const updatedMessage = {
        ...latestMessage,
        content: latestMessage.content + chunk
      };
      return [...messages.slice(0, -1), updatedMessage];
    });
  }

  async function sendMessage(message: string) {
    setWorking(true);
    addMessage({
      role: 'user',
      content: message
    });
    let first = true;
    AssistantService.chat(chatId, message)
      .onNext(token => {
        if (first && token) {
          addMessage({
            role: 'assistant',
            content: token
          });
          first = false;
        } else if (token) {
          appendToLatestMessage(token);
        }
      })
      .onError(() => setWorking(false))
      .onComplete(() => setWorking(false));
  }

  return (
    <SplitLayout className="h-full">
      <div className="flex flex-col gap-m p-m box-border h-full" style={{width: '70%'}}>
        <div className="bg-primary-10 p-m rounded-l">
          <h2 className="text-xl font-bold m-0">Loan Management Dashboard</h2>
          <p className="text-secondary m-0">View and manage loan applications</p>
        </div>
        <div className="flex-grow overflow-auto">
          <Grid items={loans} className="h-full" theme="row-stripes">
            <GridColumn path="loanNumber" autoWidth header="Loan No" className="font-medium"/>
            <GridColumn path="firstName" autoWidth header="First Name"/>
            <GridColumn path="lastName" autoWidth header="Last Name"/>
            <GridColumn path="date" autoWidth header="Date"/>
            <GridColumn path="totalAmount" autoWidth header="Total Amount"/>
            <GridColumn path="paidAmount" autoWidth header="Paid Amount"/>
            <GridColumn path="loanStatus" autoWidth header="Status">
              {({ item }) => {
                const statusStyles: Record<LoanStatus, string> = {
                  APPROVED: "text-success",
                  PENDING: "text-warning",
                  REJECTED: "text-error",
                  CANCELLED: "text-error",
                  PAID: "text-success"
                };
                const statusIcons: Record<LoanStatus, string> = {
                  APPROVED: "✅",
                  PENDING: "⏳",
                  REJECTED: "❌",
                  CANCELLED: "🚫",
                  PAID: "💰"
                };
                const status = item.loanStatus as LoanStatus;
                return (
                  <span className={statusStyles[status] || "text-secondary"}>
                    {statusIcons[status] || "❓"} {status}
                  </span>
                );
              }}
            </GridColumn>
            <GridColumn path="loanType" autoWidth header="Loan Type"/>
          </Grid>
        </div>
      </div>
      <div className="flex flex-col gap-m p-m box-border h-full bg-contrast-5" style={{width: '30%'}}>
        <div className="bg-primary-10 p-m rounded-l">
          <h2 className="text-xl font-bold m-0">AI Assistant</h2>
          <p className="text-secondary m-0">Get help with your loans</p>
        </div>
        <MessageList messages={messages} className="flex-grow overflow-auto"/>
        <div className="sticky bottom-0 bg-contrast-5 p-m">
          <MessageInput 
            onSubmit={e => sendMessage(e.detail.value)} 
            className="w-full" 
            disabled={working}
          />
        </div>
      </div>
    </SplitLayout>
  );
}
