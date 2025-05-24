import Markdown from "react-markdown";

export interface MessageItem {
  role: 'user' | 'assistant';
  content: string;
}

interface MessageProps {
  message: MessageItem;
}

export default function Message({message}: MessageProps) {
  const isUser = message.role === 'user';

  return (
    <div className={`mb-l flex ${isUser ? 'justify-end' : 'justify-start'}`}>
      <div className={`flex gap-s ${isUser ? 'flex-row-reverse' : 'flex-row'}`}>
        {/* Avatar Placeholder */}
        <div className={`flex-shrink-0 w-m h-m rounded-full ${isUser ? 'bg-primary-20' : 'bg-success-20'} flex items-center justify-center text-contrast-10`}>
          <span className="material-icons text-xl">{isUser ? 'person' : 'smart_toy'}</span>
        </div>

        <div className="flex flex-col">
          {/* Sender Name */}
          <div className={`text-xs text-secondary mb-xs ${isUser ? 'text-right' : 'text-left'}`}>
            {isUser ? 'You' : 'ABM Assistant'}
          </div>

          {/* Message Bubble */}
          <div className={`p-m rounded-lg max-w-lg ${isUser ? 'bg-primary-10 text-primary' : 'bg-contrast-5 text-body'}`}>
            <Markdown>{message.content}</Markdown>
          </div>
        </div>
      </div>
    </div>
  );
};
