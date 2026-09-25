import React, { useState } from 'react';
import {
  Sparkles,
  HelpCircle,
  MapPin,
  Calendar,
  Phone,
  Gift,
  ArrowRight,
  RefreshCw,
  AlertCircle,
  MessageSquare,
  CheckCircle2,
  Check,
  Edit3,
  Image as ImageIcon
} from 'lucide-react';
import { api } from '../services/api';
import { CampusLocation, ItemCategory } from '../types';

interface ReportLostViewProps {
  locations: CampusLocation[];
  categories: ItemCategory[];
  onSuccess: (lostId: number) => void;
  onNavigate: (view: string) => void;
}

const SAMPLE_PROMPTS = [
  "I lost a black Nike backpack near the library yesterday. It had a small red keychain.",
  "Misplaced my blue Hydro Flask water bottle around the sports complex basketball court.",
  "Lost my student ID badge with blue lanyard near Academic Block A turnstiles.",
  "Left my brown leather wallet at table 14 in the student cafeteria."
];

export const ReportLostView: React.FC<ReportLostViewProps> = ({
  locations,
  categories,
  onSuccess,
  onNavigate
}) => {
  const [currentStep, setCurrentStep] = useState<number>(1);
  const [naturalQuery, setNaturalQuery] = useState('');
  const [isProcessingAi, setIsProcessingAi] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [aiStructuredData, setAiStructuredData] = useState<any | null>(null);

  // Structured form state
  const [title, setTitle] = useState('');
  const [category, setCategory] = useState('');
  const [color, setColor] = useState('');
  const [brand, setBrand] = useState('');
  const [distinctiveFeatures, setDistinctiveFeatures] = useState('');
  const [description, setDescription] = useState('');
  const [locationId, setLocationId] = useState<number>(locations[0]?.id || 1);
  const [specificArea, setSpecificArea] = useState('');
  const [lostDate, setLostDate] = useState<string>(new Date().toISOString().slice(0, 16));
  const [rewardInfo, setRewardInfo] = useState('');
  const [contactPreference, setContactPreference] = useState('In-App Notification');
  const [referenceImageUrl, setReferenceImageUrl] = useState('');

  // Natural Language AI structuring
  const handleAiAssist = async (textToProcess: string) => {
    if (!textToProcess.trim()) return;
    setIsProcessingAi(true);
    setErrorMsg(null);
    try {
      const resp = await api.assistDescription(textToProcess);
      setAiStructuredData(resp);

      setTitle(resp.brand ? `${resp.color || ''} ${resp.category} (${resp.brand})`.trim() : `${resp.color || ''} ${resp.category}`.trim());
      setCategory(resp.category || 'Other');
      setColor(resp.color !== 'Unknown' && resp.color ? resp.color : '');
      setBrand(resp.brand || '');
      setDescription(resp.structuredDescription || textToProcess);

      if (resp.suggestedLocation) {
        const loc = locations.find(l => l.name.toLowerCase().includes(resp.suggestedLocation.toLowerCase()));
        if (loc) setLocationId(loc.id);
      }

      setCurrentStep(2);
    } catch (err: any) {
      console.error(err);
      setErrorMsg('AI assistant temporarily offline. Please fill fields below.');
      setCurrentStep(2);
    } finally {
      setIsProcessingAi(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title || !category || !locationId || !description) {
      setErrorMsg('Please ensure title, category, description, and campus location are filled.');
      return;
    }

    setIsSubmitting(true);
    setErrorMsg(null);

    try {
      const payload = {
        title,
        category,
        color,
        brand,
        distinctiveFeatures,
        description,
        locationId,
        specificArea,
        lostDate: new Date(lostDate).toISOString(),
        referenceImageUrl: referenceImageUrl || undefined,
        rewardInfo: rewardInfo || undefined,
        contactPreference
      };

      const result = await api.createLostReport(payload);
      onSuccess(result.id);
      onNavigate('matches');
    } catch (err: any) {
      console.error(err);
      setErrorMsg(err.message || 'Failed to submit lost report');
    } finally {
      setIsSubmitting(false);
    }
  };

  const stepLabels = [
    { num: 1, title: 'Describe' },
    { num: 2, title: 'AI Structured' },
    { num: 3, title: 'Details' },
    { num: 4, title: 'Submit' }
  ];

  return (
    <div className="max-w-4xl mx-auto px-4 py-10">
      {/* Banner */}
      <div className="text-center max-w-2xl mx-auto mb-8">
        <div className="inline-flex items-center space-x-2 bg-sky-50 text-sky-700 px-3.5 py-1.5 rounded-full text-xs font-bold mb-3 border border-sky-200">
          <Sparkles className="w-4 h-4 text-sky-600" />
          <span>Conversational AI Description Assistant</span>
        </div>
        <h1 className="text-3xl sm:text-4xl font-black text-slate-900 tracking-tight">
          Report a Lost Item
        </h1>
        <p className="mt-2 text-sm text-slate-600 font-medium">
          Describe what you lost in everyday language. AI structures it and scans all campus found reports for matches.
        </p>
      </div>

      {/* Colorful Stepper Indicator */}
      <div className="mb-8 p-4 bg-white rounded-3xl border border-slate-200/90 shadow-sm">
        <div className="grid grid-cols-4 gap-2">
          {stepLabels.map((s) => {
            const isCurrent = currentStep === s.num;
            const isCompleted = currentStep > s.num;
            return (
              <div key={s.num} className="flex flex-col items-center text-center">
                <div
                  className={`w-9 h-9 rounded-2xl flex items-center justify-center font-black text-xs transition-all ${
                    isCompleted
                      ? 'bg-emerald-500 text-white shadow-sm shadow-emerald-500/30'
                      : isCurrent
                      ? 'bg-sky-500 text-white shadow-md shadow-sky-500/30 ring-4 ring-sky-100'
                      : 'bg-slate-100 text-slate-400'
                  }`}
                >
                  {isCompleted ? <Check className="w-4 h-4" /> : s.num}
                </div>
                <span className={`text-[11px] font-bold mt-1.5 ${isCurrent ? 'text-sky-700' : isCompleted ? 'text-emerald-700' : 'text-slate-400'}`}>
                  {s.title}
                </span>
              </div>
            );
          })}
        </div>
      </div>

      {errorMsg && (
        <div className="mb-6 p-4 rounded-2xl bg-red-50 border border-red-200 flex items-start space-x-3 text-red-800 text-sm">
          <AlertCircle className="w-5 h-5 flex-shrink-0 text-red-600 mt-0.5" />
          <div>
            <p className="font-bold">Notice</p>
            <p>{errorMsg}</p>
          </div>
        </div>
      )}

      {/* Natural Language Prompt Prominent Box */}
      <div className="bg-gradient-to-br from-sky-500 via-blue-600 to-indigo-700 rounded-3xl p-6 sm:p-8 text-white shadow-xl shadow-sky-500/15 mb-8 relative overflow-hidden">
        <div className="relative z-10">
          <div className="flex items-center space-x-2 text-sky-200 text-xs font-black uppercase tracking-wider mb-2">
            <Sparkles className="w-4 h-4 text-cyan-300" />
            <span>Natural-Language Assistant</span>
          </div>
          <h2 className="text-xl font-black text-white mb-2">
            "I lost a black Nike backpack near the library yesterday..."
          </h2>
          <p className="text-xs text-sky-100 mb-4 max-w-xl">
            Type anything you remember — color, location, brand, distinctive keychain or scratch. AI will extract and structure each field.
          </p>

          <div className="flex flex-col sm:flex-row gap-3">
            <input
              type="text"
              value={naturalQuery}
              onChange={(e) => setNaturalQuery(e.target.value)}
              placeholder="e.g. I lost my black Nike backpack near the library yesterday..."
              className="flex-1 px-4 py-3 bg-white/15 border border-white/25 rounded-2xl text-white placeholder-white/60 text-sm focus:outline-none focus:ring-2 focus:ring-cyan-300 backdrop-blur-md font-medium"
            />
            <button
              type="button"
              onClick={() => handleAiAssist(naturalQuery)}
              disabled={isProcessingAi || !naturalQuery.trim()}
              className="px-6 py-3 rounded-2xl bg-cyan-400 hover:bg-cyan-300 text-slate-900 font-black text-xs uppercase tracking-wider transition-all flex items-center justify-center space-x-2 shadow-lg shadow-cyan-400/20 flex-shrink-0"
            >
              {isProcessingAi ? (
                <>
                  <RefreshCw className="w-4 h-4 animate-spin" />
                  <span>Structuring...</span>
                </>
              ) : (
                <>
                  <Sparkles className="w-4 h-4 text-slate-900" />
                  <span>Structure with AI</span>
                </>
              )}
            </button>
          </div>

          {/* Quick Prompts */}
          <div className="mt-4 pt-3 border-t border-white/15">
            <p className="text-[11px] text-white/70 font-semibold mb-2">Try a sample campus description:</p>
            <div className="flex flex-wrap gap-2">
              {SAMPLE_PROMPTS.map((prompt, i) => (
                <button
                  key={i}
                  type="button"
                  onClick={() => {
                    setNaturalQuery(prompt);
                    handleAiAssist(prompt);
                  }}
                  className="px-3 py-1.5 rounded-xl bg-white/15 hover:bg-white/25 text-white text-xs font-medium transition-colors text-left"
                >
                  "{prompt.slice(0, 48)}..."
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* AI STRUCTURED Card Preview */}
      {aiStructuredData && (
        <div className="mb-8 p-6 rounded-3xl bg-gradient-to-br from-emerald-50/70 to-teal-50/50 border border-emerald-200 shadow-sm animate-in fade-in slide-in-from-top-2 duration-300">
          <div className="flex items-center justify-between mb-4">
            <span className="text-xs font-black text-emerald-800 flex items-center space-x-1.5 uppercase tracking-wider">
              <Sparkles className="w-4 h-4 text-emerald-600" />
              <span>AI STRUCTURED RESULT</span>
            </span>
            <span className="text-xs text-emerald-700 font-semibold">Review & edit details below</span>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs mb-4">
            <div className="p-3 bg-white rounded-2xl border border-emerald-100 shadow-sm">
              <span className="text-slate-400 block font-bold text-[10px] uppercase">Category</span>
              <strong className="text-emerald-800 text-sm">{category || 'Other'}</strong>
            </div>
            <div className="p-3 bg-white rounded-2xl border border-emerald-100 shadow-sm">
              <span className="text-slate-400 block font-bold text-[10px] uppercase">Color</span>
              <strong className="text-sky-800 text-sm">{color || 'Unknown'}</strong>
            </div>
            <div className="p-3 bg-white rounded-2xl border border-emerald-100 shadow-sm">
              <span className="text-slate-400 block font-bold text-[10px] uppercase">Brand</span>
              <strong className="text-indigo-800 text-sm">{brand || 'Unbranded'}</strong>
            </div>
            <div className="p-3 bg-white rounded-2xl border border-emerald-100 shadow-sm">
              <span className="text-slate-400 block font-bold text-[10px] uppercase">Location Clue</span>
              <strong className="text-amber-800 text-sm">{aiStructuredData.suggestedLocation || 'Campus'}</strong>
            </div>
          </div>

          <p className="text-xs text-slate-600 bg-white/80 p-3 rounded-xl border border-emerald-100">
            <strong>Structured summary: </strong> {aiStructuredData.structuredDescription}
          </p>
        </div>
      )}

      {/* Main Report Form */}
      <form onSubmit={handleSubmit} className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-sm space-y-6">
        <div>
          <span className="text-xs font-black text-sky-600 uppercase tracking-widest">Report Details</span>
          <h2 className="text-lg font-bold text-slate-900">Confirm & Edit Lost Item Information</h2>
          <p className="text-xs text-slate-500">You can edit any field structured by AI.</p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
          <div className="sm:col-span-2">
            <label className="block text-xs font-bold text-slate-700 mb-1.5">Item Title / Summary *</label>
            <input
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g. Black Nike Backpack with Red Zipper Pull"
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">Category *</label>
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
            >
              <option value="">Select Category</option>
              {categories.map((c) => (
                <option key={c.id} value={c.name}>{c.name}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">Color</label>
            <input
              type="text"
              value={color}
              onChange={(e) => setColor(e.target.value)}
              placeholder="e.g. Black, Navy Blue, Silver"
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">Brand / Maker</label>
            <input
              type="text"
              value={brand}
              onChange={(e) => setBrand(e.target.value)}
              placeholder="e.g. Nike, Apple, Hydro Flask"
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">Distinctive Features (Keychains, Stickers)</label>
            <input
              type="text"
              value={distinctiveFeatures}
              onChange={(e) => setDistinctiveFeatures(e.target.value)}
              placeholder="e.g. Red keychain, scratch on base"
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
            />
          </div>

          <div className="sm:col-span-2">
            <label className="block text-xs font-bold text-slate-700 mb-1.5">Full Description & Circumstances *</label>
            <textarea
              rows={3}
              required
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Describe where you were, what was inside, or any details to help campus security identify it."
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">Last Seen Campus Location *</label>
            <select
              value={locationId}
              onChange={(e) => setLocationId(Number(e.target.value))}
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
            >
              {locations.map((loc) => (
                <option key={loc.id} value={loc.id}>{loc.name} ({loc.zone})</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">Specific Floor or Room</label>
            <input
              type="text"
              value={specificArea}
              onChange={(e) => setSpecificArea(e.target.value)}
              placeholder="e.g. 2nd Floor Study Room 204"
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">Approximate Lost Date & Time *</label>
            <input
              type="datetime-local"
              required
              value={lostDate}
              onChange={(e) => setLostDate(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">Optional Reward Note</label>
            <input
              type="text"
              value={rewardInfo}
              onChange={(e) => setRewardInfo(e.target.value)}
              placeholder="e.g. ₹500 or Coffee reward"
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
            />
          </div>

          <div className="sm:col-span-2">
            <label className="block text-xs font-bold text-slate-700 mb-1.5">Reference Photo URL (Optional)</label>
            <input
              type="url"
              value={referenceImageUrl}
              onChange={(e) => setReferenceImageUrl(e.target.value)}
              placeholder="https://..."
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
            />
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex items-center justify-end space-x-4 pt-4 border-t border-slate-100">
          <button
            type="button"
            onClick={() => onNavigate('browse')}
            className="px-6 py-3 rounded-2xl border border-slate-300 text-slate-700 text-sm font-bold hover:bg-slate-100 transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={isSubmitting}
            className="px-8 py-3.5 rounded-2xl bg-gradient-to-r from-sky-500 to-indigo-600 hover:from-sky-600 hover:to-indigo-700 text-white text-sm font-bold shadow-lg shadow-sky-500/25 hover:shadow-sky-500/40 hover:-translate-y-0.5 active:translate-y-0 transition-all flex items-center space-x-2"
          >
            {isSubmitting ? (
              <>
                <RefreshCw className="w-4 h-4 animate-spin" />
                <span>Searching Matches...</span>
              </>
            ) : (
              <>
                <span>Publish Report & Scan Matches</span>
                <ArrowRight className="w-4 h-4" />
              </>
            )}
          </button>
        </div>
      </form>
    </div>
  );
};
