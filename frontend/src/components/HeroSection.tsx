import React from 'react';
import {
  Sparkles,
  Search,
  PlusCircle,
  HelpCircle,
  ShieldCheck,
  QrCode,
  ArrowRight,
  Eye,
  Cpu,
  CheckCircle2
} from 'lucide-react';

interface HeroSectionProps {
  onNavigate: (view: string) => void;
  stats?: {
    activeFound: number;
    activeLost: number;
    recoveryRate: number;
  };
}

export const HeroSection: React.FC<HeroSectionProps> = ({ onNavigate, stats }) => {
  return (
    <div className="relative overflow-hidden bg-gradient-to-b from-brand-50/60 via-white to-slate-50 py-12 lg:py-16 border-b border-slate-200">
      {/* Decorative Blur Backgrounds */}
      <div className="absolute -top-24 -left-20 w-96 h-96 bg-brand-200/40 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute top-1/2 -right-20 w-96 h-96 bg-indigo-200/30 rounded-full blur-3xl pointer-events-none" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div className="text-center max-w-3xl mx-auto">
          {/* AI Banner Pill */}
          <div className="inline-flex items-center space-x-2 bg-brand-100/80 border border-brand-200 text-brand-800 text-xs font-semibold px-3 py-1 rounded-full mb-6 shadow-sm">
            <Sparkles className="w-3.5 h-3.5 text-brand-600" />
            <span>AI-Powered Computer Vision & Multi-Signal Semantic Matching</span>
          </div>

          <h1 className="text-4xl sm:text-5xl lg:text-6xl font-black text-slate-900 tracking-tight leading-[1.15]">
            Lost something on campus?
            <br />
            <span className="bg-gradient-to-r from-brand-600 via-indigo-600 to-cyan-500 text-transparent bg-clip-text">
              Found something that isn't yours?
            </span>
          </h1>

          <p className="mt-5 text-base sm:text-lg text-slate-600 leading-relaxed max-w-2xl mx-auto">
            CampusFind AI automatically identifies found items via image understanding, bridges owners using
            explainable semantic matching, prevents fraud with blind verification, and guarantees contactless QR pickup.
          </p>

          {/* Major Dual Action Buttons */}
          <div className="mt-8 flex flex-col sm:flex-row items-center justify-center gap-4">
            <button
              onClick={() => onNavigate('report-found')}
              className="w-full sm:w-auto px-7 py-3.5 rounded-2xl bg-gradient-to-r from-emerald-500 to-teal-600 text-white font-bold text-base shadow-lg shadow-emerald-500/25 hover:shadow-emerald-500/40 hover:-translate-y-0.5 active:translate-y-0 transition-all flex items-center justify-center space-x-2.5 group"
            >
              <PlusCircle className="w-5 h-5 text-emerald-100 group-hover:rotate-90 transition-transform duration-300" />
              <span>REPORT FOUND ITEM</span>
              <span className="bg-white/20 text-white text-xs px-2 py-0.5 rounded-full font-bold">Photo Upload</span>
            </button>

            <button
              onClick={() => onNavigate('report-lost')}
              className="w-full sm:w-auto px-7 py-3.5 rounded-2xl bg-gradient-to-r from-brand-600 to-indigo-600 text-white font-bold text-base shadow-lg shadow-brand-500/25 hover:shadow-brand-500/40 hover:-translate-y-0.5 active:translate-y-0 transition-all flex items-center justify-center space-x-2.5"
            >
              <HelpCircle className="w-5 h-5 text-brand-100" />
              <span>REPORT LOST ITEM</span>
              <ArrowRight className="w-4 h-4 text-brand-200" />
            </button>

            <button
              onClick={() => onNavigate('browse')}
              className="w-full sm:w-auto px-6 py-3.5 rounded-2xl bg-white hover:bg-slate-100 text-slate-800 font-bold text-base border border-slate-300 shadow-sm transition-all flex items-center justify-center space-x-2"
            >
              <Search className="w-4 h-4 text-slate-500" />
              <span>Browse Catalog</span>
            </button>
          </div>

          {/* Live Campus Trust Stats Pill */}
          <div className="mt-8 inline-flex flex-wrap items-center justify-center gap-6 px-6 py-2.5 bg-white/80 backdrop-blur rounded-2xl border border-slate-200 shadow-sm text-xs font-semibold text-slate-700">
            <div className="flex items-center space-x-1.5">
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-ping" />
              <span className="font-bold text-emerald-700">{stats?.recoveryRate || 94.2}%</span>
              <span className="text-slate-500">Recovery Rate</span>
            </div>
            <div className="hidden sm:block w-px h-4 bg-slate-200" />
            <div className="flex items-center space-x-1.5">
              <span className="font-bold text-brand-700">{stats?.activeFound || 4}</span>
              <span className="text-slate-500">Found Items Logged</span>
            </div>
            <div className="hidden sm:block w-px h-4 bg-slate-200" />
            <div className="flex items-center space-x-1.5">
              <span className="font-bold text-indigo-700">&lt; 24h</span>
              <span className="text-slate-500">Average Return Time</span>
            </div>
          </div>
        </div>

        {/* 5-Step Process Cards */}
        <div className="mt-14">
          <div className="text-center mb-8">
            <h2 className="text-xs font-bold text-brand-600 uppercase tracking-widest">How It Works</h2>
            <p className="text-2xl font-black text-slate-900 mt-1">Five Simple Steps from Lost to Returned</p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
            {/* Step 1 */}
            <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-sm hover:shadow-md transition-shadow relative">
              <div className="w-8 h-8 rounded-xl bg-blue-100 text-blue-700 flex items-center justify-center font-black text-sm mb-3">
                1
              </div>
              <h3 className="font-bold text-slate-900 text-sm">Upload or Describe</h3>
              <p className="text-xs text-slate-600 mt-1 leading-relaxed">
                Take a photo of the item or describe what you lost in simple natural language.
              </p>
            </div>

            {/* Step 2 */}
            <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-sm hover:shadow-md transition-shadow relative">
              <div className="w-8 h-8 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center font-black text-sm mb-3">
                2
              </div>
              <h3 className="font-bold text-slate-900 text-sm">AI Identifies Details</h3>
              <p className="text-xs text-slate-600 mt-1 leading-relaxed">
                Multimodal AI categorizes brand, color, materials, and extracts distinctive features.
              </p>
            </div>

            {/* Step 3 */}
            <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-sm hover:shadow-md transition-shadow relative">
              <div className="w-8 h-8 rounded-xl bg-indigo-100 text-indigo-700 flex items-center justify-center font-black text-sm mb-3">
                3
              </div>
              <h3 className="font-bold text-slate-900 text-sm">Semantic Matching</h3>
              <p className="text-xs text-slate-600 mt-1 leading-relaxed">
                Hybrid engine compares location, date, color, brand, and text vectors with explainable breakdown.
              </p>
            </div>

            {/* Step 4 */}
            <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-sm hover:shadow-md transition-shadow relative">
              <div className="w-8 h-8 rounded-xl bg-amber-100 text-amber-700 flex items-center justify-center font-black text-sm mb-3">
                4
              </div>
              <h3 className="font-bold text-slate-900 text-sm">Blind Verification</h3>
              <p className="text-xs text-slate-600 mt-1 leading-relaxed">
                Claimant answers questions about hidden secrets (e.g. inner contents) to prevent fraud.
              </p>
            </div>

            {/* Step 5 */}
            <div className="bg-white rounded-2xl p-5 border border-slate-200/80 shadow-sm hover:shadow-md transition-shadow relative">
              <div className="w-8 h-8 rounded-xl bg-purple-100 text-purple-700 flex items-center justify-center font-black text-sm mb-3">
                5
              </div>
              <h3 className="font-bold text-slate-900 text-sm">QR Pickup & Receipt</h3>
              <p className="text-xs text-slate-600 mt-1 leading-relaxed">
                Staff scans one-time QR code or enters 6-digit OTP to complete verified handover.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
