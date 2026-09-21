import React from 'react';
import { Sparkles, UserCheck, Shield, RefreshCw } from 'lucide-react';
import { User } from '../types';

interface DemoBannerProps {
  currentUser: User | null;
  onSwitchUser: (email: string) => void;
  onNavigate: (view: string) => void;
}

export const DemoBanner: React.FC<DemoBannerProps> = ({
  currentUser,
  onSwitchUser,
  onNavigate
}) => {
  return (
    <div className="fixed bottom-4 left-1/2 -translate-x-1/2 z-40 bg-slate-900/90 backdrop-blur-md text-white px-5 py-2.5 rounded-full shadow-2xl border border-slate-700/80 flex items-center space-x-4 text-xs">
      <div className="flex items-center space-x-1.5 font-bold text-cyan-400">
        <Sparkles className="w-4 h-4 text-cyan-400" />
        <span className="hidden sm:inline">DEMO ROLE:</span>
      </div>

      <div className="flex items-center space-x-1 bg-slate-800 p-1 rounded-full border border-slate-700">
        <button
          onClick={() => onSwitchUser('student@campus.edu')}
          className={`px-3 py-1 rounded-full font-bold transition-all text-[11px] ${
            currentUser?.email === 'student@campus.edu'
              ? 'bg-brand-600 text-white shadow'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          Student (Alex)
        </button>
        <button
          onClick={() => onSwitchUser('staff@campus.edu')}
          className={`px-3 py-1 rounded-full font-bold transition-all text-[11px] ${
            currentUser?.email === 'staff@campus.edu'
              ? 'bg-emerald-600 text-white shadow'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          Staff (Sarah)
        </button>
        <button
          onClick={() => onSwitchUser('admin@campus.edu')}
          className={`px-3 py-1 rounded-full font-bold transition-all text-[11px] ${
            currentUser?.email === 'admin@campus.edu'
              ? 'bg-purple-600 text-white shadow'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          Admin
        </button>
      </div>

      <div className="hidden md:flex items-center space-x-2 pl-2 border-l border-slate-700">
        <button
          onClick={() => onNavigate('report-found')}
          className="hover:text-cyan-300 font-semibold"
        >
          Try AI Vision
        </button>
        <span>•</span>
        <button
          onClick={() => onNavigate('matches')}
          className="hover:text-cyan-300 font-semibold"
        >
          AI Matches
        </button>
        <span>•</span>
        <button
          onClick={() => onNavigate('pickup')}
          className="hover:text-cyan-300 font-semibold"
        >
          QR & OTP Desk
        </button>
      </div>
    </div>
  );
};
