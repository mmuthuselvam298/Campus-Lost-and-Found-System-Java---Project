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
  CheckCircle2,
  FileText,
  MapPin,
  Clock
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
    <div className="relative overflow-hidden bg-gradient-to-b from-sky-50/70 via-white to-[#f8fafc] py-14 lg:py-20 border-b border-slate-200/80">
      {/* Decorative Cheerful Blur Backgrounds */}
      <div className="absolute -top-24 -left-20 w-96 h-96 bg-sky-200/40 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute top-1/3 -right-20 w-96 h-96 bg-amber-100/50 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute -bottom-24 left-1/3 w-80 h-80 bg-emerald-100/40 rounded-full blur-3xl pointer-events-none" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div className="text-center max-w-3xl mx-auto">
          {/* AI Banner Pill */}
          <div className="inline-flex items-center space-x-2 bg-gradient-to-r from-sky-100 via-indigo-50 to-emerald-100 border border-sky-200 text-sky-900 text-xs font-bold px-3.5 py-1.5 rounded-full mb-6 shadow-sm">
            <Sparkles className="w-4 h-4 text-sky-600 animate-spin-slow" />
            <span>Smart Campus Computer Vision & Multi-Signal AI Matching</span>
          </div>

          {/* Large Hero Heading */}
          <h1 className="text-4xl sm:text-5xl lg:text-6xl font-black text-slate-900 tracking-tight leading-[1.12]">
            LOST SOMETHING?
            <br />
            <span className="bg-gradient-to-r from-sky-600 via-indigo-600 to-teal-500 text-transparent bg-clip-text">
              WE'LL HELP YOU FIND IT.
            </span>
          </h1>

          {/* Supporting Text */}
          <p className="mt-5 text-base sm:text-lg text-slate-600 leading-relaxed max-w-2xl mx-auto font-medium">
            Report a lost or found item, let AI help describe it, and get matched with potential results across campus.
          </p>

          {/* Two Main Visually Prominent Actions */}
          <div className="mt-9 flex flex-col sm:flex-row items-center justify-center gap-4">
            <button
              onClick={() => onNavigate('report-lost')}
              className="w-full sm:w-auto px-8 py-4 rounded-2xl bg-gradient-to-r from-sky-500 via-blue-600 to-indigo-600 hover:from-sky-600 hover:to-indigo-700 text-white font-black text-base shadow-xl shadow-sky-500/25 hover:shadow-sky-500/40 hover:-translate-y-1 active:translate-y-0 transition-all flex items-center justify-center space-x-3 group"
            >
              <HelpCircle className="w-5 h-5 text-sky-100 group-hover:scale-110 transition-transform" />
              <span>REPORT LOST ITEM</span>
              <ArrowRight className="w-4 h-4 text-sky-200" />
            </button>

            <button
              onClick={() => onNavigate('report-found')}
              className="w-full sm:w-auto px-8 py-4 rounded-2xl bg-gradient-to-r from-emerald-500 via-teal-500 to-emerald-600 hover:from-emerald-600 hover:to-teal-700 text-white font-black text-base shadow-xl shadow-emerald-500/25 hover:shadow-emerald-500/40 hover:-translate-y-1 active:translate-y-0 transition-all flex items-center justify-center space-x-3 group"
            >
              <PlusCircle className="w-5 h-5 text-emerald-100 group-hover:rotate-90 transition-transform duration-300" />
              <span>REPORT FOUND ITEM</span>
              <span className="bg-white/20 text-white text-[11px] px-2 py-0.5 rounded-full font-bold">Snap & Upload</span>
            </button>

            <button
              onClick={() => onNavigate('browse')}
              className="w-full sm:w-auto px-6 py-4 rounded-2xl bg-white hover:bg-slate-50 text-slate-700 font-bold text-base border-2 border-slate-200 hover:border-slate-300 shadow-sm transition-all flex items-center justify-center space-x-2"
            >
              <Search className="w-4 h-4 text-slate-500" />
              <span>Browse Items</span>
            </button>
          </div>

          {/* Live Campus Trust Stats Pill */}
          <div className="mt-10 inline-flex flex-wrap items-center justify-center gap-6 px-7 py-3 bg-white/95 backdrop-blur-md rounded-2xl border border-slate-200/80 shadow-md shadow-slate-100 text-xs font-semibold text-slate-700">
            <div className="flex items-center space-x-2">
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse" />
              <span className="font-black text-emerald-700 text-sm">{stats?.recoveryRate || 94.2}%</span>
              <span className="text-slate-500">Recovery Rate</span>
            </div>
            <div className="hidden sm:block w-px h-4 bg-slate-200" />
            <div className="flex items-center space-x-2">
              <span className="font-black text-sky-700 text-sm">{stats?.activeFound || 4}</span>
              <span className="text-slate-500">Active Found Reports</span>
            </div>
            <div className="hidden sm:block w-px h-4 bg-slate-200" />
            <div className="flex items-center space-x-2">
              <span className="font-black text-indigo-700 text-sm">&lt; 24h</span>
              <span className="text-slate-500">Avg Return Speed</span>
            </div>
          </div>
        </div>

        {/* 5-Step Process Flow with Cheerful Colorful Cards */}
        <div className="mt-16">
          <div className="text-center mb-8">
            <span className="px-3 py-1 rounded-full bg-indigo-50 border border-indigo-100 text-indigo-700 text-xs font-black uppercase tracking-widest">
              Simple 5-Step Journey
            </span>
            <p className="text-2xl sm:text-3xl font-black text-slate-900 mt-2">
              How CampusFind Reconnects You With Your Item
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4 relative">
            {/* Step 1 */}
            <div className="bg-white rounded-3xl p-5 border-2 border-sky-100 shadow-sm hover:shadow-md hover:border-sky-300 transition-all flex flex-col justify-between group">
              <div>
                <div className="w-10 h-10 rounded-2xl bg-sky-100 text-sky-700 flex items-center justify-center font-black text-base mb-3 group-hover:scale-105 transition-transform">
                  1
                </div>
                <h3 className="font-extrabold text-slate-900 text-sm">Upload / Describe</h3>
                <p className="text-xs text-slate-500 mt-1.5 leading-relaxed font-medium">
                  Snap a photo of the item or write a natural-language description of what you lost.
                </p>
              </div>
              <div className="mt-4 pt-3 border-t border-slate-100 flex items-center text-[11px] font-bold text-sky-600">
                <span>Start report</span>
                <ArrowRight className="w-3.5 h-3.5 ml-1" />
              </div>
            </div>

            {/* Step 2 */}
            <div className="bg-white rounded-3xl p-5 border-2 border-emerald-100 shadow-sm hover:shadow-md hover:border-emerald-300 transition-all flex flex-col justify-between group">
              <div>
                <div className="w-10 h-10 rounded-2xl bg-emerald-100 text-emerald-700 flex items-center justify-center font-black text-base mb-3 group-hover:scale-105 transition-transform">
                  2
                </div>
                <h3 className="font-extrabold text-slate-900 text-sm">AI Understands</h3>
                <p className="text-xs text-slate-500 mt-1.5 leading-relaxed font-medium">
                  Multimodal vision identifies category, brand, colors, and extracts distinctive marks.
                </p>
              </div>
              <div className="mt-4 pt-3 border-t border-slate-100 flex items-center text-[11px] font-bold text-emerald-600">
                <span>Automated tag</span>
                <Sparkles className="w-3.5 h-3.5 ml-1" />
              </div>
            </div>

            {/* Step 3 */}
            <div className="bg-white rounded-3xl p-5 border-2 border-indigo-100 shadow-sm hover:shadow-md hover:border-indigo-300 transition-all flex flex-col justify-between group">
              <div>
                <div className="w-10 h-10 rounded-2xl bg-indigo-100 text-indigo-700 flex items-center justify-center font-black text-base mb-3 group-hover:scale-105 transition-transform">
                  3
                </div>
                <h3 className="font-extrabold text-slate-900 text-sm">Smart Matching</h3>
                <p className="text-xs text-slate-500 mt-1.5 leading-relaxed font-medium">
                  Hybrid engine cross-references locations, dates, descriptions, and visual signals.
                </p>
              </div>
              <div className="mt-4 pt-3 border-t border-slate-100 flex items-center text-[11px] font-bold text-indigo-600">
                <span>Explainable match</span>
                <ArrowRight className="w-3.5 h-3.5 ml-1" />
              </div>
            </div>

            {/* Step 4 */}
            <div className="bg-white rounded-3xl p-5 border-2 border-amber-100 shadow-sm hover:shadow-md hover:border-amber-300 transition-all flex flex-col justify-between group">
              <div>
                <div className="w-10 h-10 rounded-2xl bg-amber-100 text-amber-700 flex items-center justify-center font-black text-base mb-3 group-hover:scale-105 transition-transform">
                  4
                </div>
                <h3 className="font-extrabold text-slate-900 text-sm">Verify</h3>
                <p className="text-xs text-slate-500 mt-1.5 leading-relaxed font-medium">
                  Blind verification prompts protect private secrets. Campus staff evaluate and approve.
                </p>
              </div>
              <div className="mt-4 pt-3 border-t border-slate-100 flex items-center text-[11px] font-bold text-amber-600">
                <span>Anti-fraud check</span>
                <ShieldCheck className="w-3.5 h-3.5 ml-1" />
              </div>
            </div>

            {/* Step 5 */}
            <div className="bg-white rounded-3xl p-5 border-2 border-purple-100 shadow-sm hover:shadow-md hover:border-purple-300 transition-all flex flex-col justify-between group">
              <div>
                <div className="w-10 h-10 rounded-2xl bg-purple-100 text-purple-700 flex items-center justify-center font-black text-base mb-3 group-hover:scale-105 transition-transform">
                  5
                </div>
                <h3 className="font-extrabold text-slate-900 text-sm">Collect</h3>
                <p className="text-xs text-slate-500 mt-1.5 leading-relaxed font-medium">
                  Show your one-time QR code or 6-digit OTP at the campus desk and receive a signed receipt.
                </p>
              </div>
              <div className="mt-4 pt-3 border-t border-slate-100 flex items-center text-[11px] font-bold text-purple-600">
                <span>QR pass ready</span>
                <QrCode className="w-3.5 h-3.5 ml-1" />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
